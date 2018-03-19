import select, socket, sys
import queue


def komennot():
   print ("iffissa")
   #pyautogui.press('a') #näppäinkomento
   #pyautogui.click(button='right') #Tämä right click


TCP_IP = socket.gethostname() # '127.0.0.1'
TCP_PORT = 3005
BUFFER_SIZE = 64  # Normally 1024, but we want fast response


server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.setblocking(0)
server.bind(("", TCP_PORT))
server.listen(5)
inputs = [server]
outputs = []
message_queues = {}



while inputs:
    #print("aloittaa")
    readable, writable, exceptional = select.select(
        inputs, [], inputs)
    """
        ongelma oli writable objecti. jos siellä on serveri, se päästää sen aina läpi kun serveri on valmis lähettämään viestiä, mikä on aina kun sillä ei ole luettavaa
    """
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
                print(data)
                if data.decode("utf-8") == "e":
                    inputs = 0
            else:
                print("remove_s")
                inputs.remove(s)
                s.close()


    for s in exceptional:
        print("exceptional")
        inputs.remove(s)
        if s in outputs:
            outputs.remove(s)
        s.close()
del message_queues[s]
server.close()
