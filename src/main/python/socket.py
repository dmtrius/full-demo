import socket
import time

# TCP connection (reliable)
def tcp_benchmark():
    start = time.time()
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(('httpbin.org', 80))
    sock.send(b'GET / HTTP/1.1\r\nHost: httpbin.org\r\n\r\n')
    response = sock.recv(1024)
    sock.close()
    return time.time() - start
# UDP connection (fast)
def udp_benchmark():
    start = time.time()
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.sendto(b'test', ('8.8.8.8', 53))
    sock.settimeout(1)
    try:
        sock.recv(1024)
    except:
        pass
    sock.close()
    return time.time() - start
print(f"TCP time: {tcp_benchmark():.3f}s")
print(f"UDP time: {udp_benchmark():.3f}s")
