#include "bluetooth.h"

extern "C" 
{
#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>
#include <bluetooth/sco.h>
#include <bluetooth/l2cap.h>
}


#include <unistd.h>
#include <sys/socket.h>

using namespace std;

sdp_session_t* registerService(uint8_t rfcomm_channel);


BT_Com::BT_Com(Logger *log) {

  m_log = log;
  m_sock = -1;
}

BT_Com::~BT_Com() {

  m_log->Info("Closing socket...");
  sdp_close(m_session);
  close(m_sock);
}

sdp_session_t* registerService(uint8_t rfcomm_channel) {
  // Adapted from http://www.btessentials.com/examples/bluez/sdp-register.c
	//uint32_t svc_uuid_int[] = {   0x01110000, 0x00100000, 0x80000080, 0xFB349B5F };
	uint32_t svc_uuid_int[] = {   0xd0c087fa, 0xde11acaf, 0x0008398a, 0x669a0c20 };
	//fa87c0d0-afac-11de-8a39-0800-200c9a66
	const char *service_name = "ms_locker";
	const char *svc_dsc = "An experimental tool";
	const char *service_prov = "MomsSpaghetti";

	uuid_t root_uuid, l2cap_uuid, rfcomm_uuid, svc_uuid,
				 svc_class_uuid;
	sdp_list_t *l2cap_list = 0,
						 *rfcomm_list = 0,
						 *root_list = 0,
						 *proto_list = 0,
						 *access_proto_list = 0,
						 *svc_class_list = 0,
						 *profile_list = 0;
	sdp_data_t *channel = 0;
	sdp_profile_desc_t profile;
	sdp_record_t record = { 0 };
	sdp_session_t *session = 0;

	// set the general service ID
	sdp_uuid128_create( &svc_uuid, &svc_uuid_int );
	sdp_set_service_id( &record, svc_uuid );

	char str[256] = "";
	sdp_uuid2strn(&svc_uuid, str, 256);
	printf("Registering UUID %s\n", str);

	// set the service class
	sdp_uuid16_create(&svc_class_uuid, SERIAL_PORT_SVCLASS_ID);
	svc_class_list = sdp_list_append(0, &svc_class_uuid);
	sdp_set_service_classes(&record, svc_class_list);

	// set the Bluetooth profile information
	sdp_uuid16_create(&profile.uuid, SERIAL_PORT_PROFILE_ID);
	profile.version = 0x0100;
	profile_list = sdp_list_append(0, &profile);
	sdp_set_profile_descs(&record, profile_list);

	// make the service record publicly browsable
	sdp_uuid16_create(&root_uuid, PUBLIC_BROWSE_GROUP);
	root_list = sdp_list_append(0, &root_uuid);
	sdp_set_browse_groups( &record, root_list );

	// set l2cap information
	sdp_uuid16_create(&l2cap_uuid, L2CAP_UUID);
	l2cap_list = sdp_list_append( 0, &l2cap_uuid );
	proto_list = sdp_list_append( 0, l2cap_list );

	// register the RFCOMM channel for RFCOMM sockets
	sdp_uuid16_create(&rfcomm_uuid, RFCOMM_UUID);
	channel = sdp_data_alloc(SDP_UINT8, &rfcomm_channel);
	rfcomm_list = sdp_list_append( 0, &rfcomm_uuid );
	sdp_list_append( rfcomm_list, channel );
	sdp_list_append( proto_list, rfcomm_list );

	access_proto_list = sdp_list_append( 0, proto_list );
	sdp_set_access_protos( &record, access_proto_list );

	// set the name, provider, and description
	sdp_set_info_attr(&record, service_name, service_prov, svc_dsc);

	// connect to the local SDP server, register the service record,
	// and disconnect
	const bdaddr_t any = {{0, 0, 0, 0, 0, 0}};
	const bdaddr_t local = {{0, 0, 0, 0xff, 0xff, 0xff}}; // BDADDR_LOCAL
	session = sdp_connect(&any, &local, SDP_RETRY_IF_BUSY);
	sdp_record_register(session, &record, 0);

	// cleanup
	sdp_data_free( channel );
	sdp_list_free( l2cap_list, 0 );
	sdp_list_free( rfcomm_list, 0 );
	sdp_list_free( root_list, 0 );
	sdp_list_free( access_proto_list, 0 );
	sdp_list_free( svc_class_list, 0 );
	sdp_list_free( profile_list, 0 );

	return session;
}

int BT_Com::init() {

  uint8_t port = 1;
  
  m_session = registerService(port);
  
  
  // allocate socket
  m_sock = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
  if (m_sock < 0) {
    m_log->Error("BT_Com: Can't create socket");
    return (-1);
  }
  
  struct sockaddr_rc loc_addr = { 0 };
  
  // bind socket to port 1 of the first available
  // local bluetooth adapter
  loc_addr.rc_family = AF_BLUETOOTH;
  memset(&loc_addr.rc_bdaddr, 0, sizeof(loc_addr.rc_bdaddr)); // BDADDR_ANY
  loc_addr.rc_channel = (uint8_t)port;
  
  if (bind(m_sock, (struct sockaddr *)&loc_addr, sizeof(loc_addr)) < 0) {
    m_log->Error("BT_Com: Can't bind to socket");
    return (-1);
  }
  
  
  m_log->Info("BT_Com: Initalization complete");
  
  return 0;
}

BT_Conn *BT_Com::waitForConnection() {

  struct sockaddr_rc rem_addr = { 0 };
  socklen_t opt = sizeof(rem_addr);

  // put socket into listening mode
  listen(m_sock, 1);
  
  // accept one connection
  int client = accept(m_sock, (struct sockaddr *)&rem_addr, &opt);
  
  if (client < 0) { 
    m_log->Info("BT_Com: couldn't accept connection");
    return NULL;
  }
  
  char buf[50];
  ba2str( &rem_addr.rc_bdaddr, buf);
  m_log->Info("BT_Com: accepted connection from " + string(buf));
  
  BT_Conn *c = new BT_Conn(client);
  
  return c;
}

int BT_Com::recv(BT_Conn *c, char *buf, size_t len) {

  if (c == NULL) return -1;

  // read data from the client
  int bytes_read = read(c->getFd(), buf, len);
  
  if (bytes_read > 0) {
    m_log->Debug("BT_Com: received [" + string(buf) + "] bytes");
  }
  
  return bytes_read;
}
  
int BT_Com::send(BT_Conn *c, char *buf, size_t len) {

  if (c == NULL) return -1;

  int status = write(c->getFd(), buf, len);
  
  if (status < 0) {
    m_log->Error("BT_Com: Couldn't send data of len " + len);
  }
  
  m_log->Debug("BT_Com: sent [" + string(buf) + "] bytes");
  
  return status;
}

void BT_Com::closeConnection(BT_Conn *c) {

  if (c == NULL) return;
  
  m_log->Debug("BT_Com: closing connection");
  
  close(c->getFd());

  delete c;
}


