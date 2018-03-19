#!/usr/bin/env python

#Hiiikomennot: http://pyautogui.readthedocs.io/en/latest/mouse.html
#Näppäinkomennot: https://stackoverflow.com/questions/13564851/how-to-generate-keyboard-events-in-python 
#ja https://stackoverflow.com/questions/136734/key-presses-in-python
#ja https://developer.android.com/reference/android/view/KeyEvent.html#KEYCODE_COMMA



import select, socket, sys
import queue
import pyautogui

def komennot():
   print ("iffissa")
   #pyautogui.press('a') #näppäinkomento
   #pyautogui.click(button='right') #Tämä right click
   pyautogui.click() #Tämä left click

TCP_IP = socket.gethostname() # '127.0.0.1'
TCP_PORT = 3005
BUFFER_SIZE = 64  # Normally 1024, but we want fast response


server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.setblocking(0)
server.bind((TCP_IP, TCP_PORT))
server.listen(5)
inputs = [server]
outputs = []
message_queues = {}



while inputs:
    #print("aloittaa")
    readable, writable, exceptional = select.select(
        inputs, outputs, inputs)
        
    #print("got some")
    for s in readable:
        print("luku")
        if s is server:
            print("serveri")
            connection, client_address = s.accept()
            connection.setblocking(0)
            inputs.append(connection)
            message_queues[connection] = queue.Queue()
        else:
            data = s.recv(BUFFER_SIZE)
            if data:
                print("sai dataa")
                message_queues[s].put(data)
                if s not in outputs:
                    outputs.append(s)
            else:
                print("remove_s")
                if s in outputs:
                    outputs.remove(s)
                inputs.remove(s)
                s.close()
                del message_queues[s]

    for s in writable:
        #print("writable")
        if not message_queues[s].empty():
            next_msg = message_queues[s].get()
            print("next message")
        
        else:
            pass
            #print("tyhjä")
            #komennot()
            

    for s in exceptional:
        print("exceptional")
        inputs.remove(s)
        if s in outputs:
            outputs.remove(s)
        s.close()
        del message_queues[s]
        
