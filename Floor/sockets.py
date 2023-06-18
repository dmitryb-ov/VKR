import os

import jpysocket
import socket
import configparser

def start():
    host = 'localhost'
    port = 4005
    s = socket.socket()
    s.bind((host, port))
    s.listen(5)
    while True:
        print("Socket Is Listening....")
        connection, address = s.accept()
        print("Connected To ", address)
        msgsend = jpysocket.jpyencode("Thank You For Connecting.")
        connection.send(msgsend)
        msgrecv = connection.recv(1024)
        msgrecv = jpysocket.jpydecode(msgrecv)
        print("From Client: ", msgrecv)

    config = configparser.ConfigParser()
    config.read(r'inifile\file.ini')
    config.set('img', 'image_path', msgrecv)

    with open(r'inifile\file.ini', 'w') as configfile:
        config.write(configfile)

    os.system(r"main.py")


if __name__ == "__main__":
    start()
