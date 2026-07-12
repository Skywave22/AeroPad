#!/usr/bin/env python3
"""
AeroPad companion receiver — run this on your PC (Windows/Mac/Linux).

  1. Install deps:  pip install pynput zeroconf
  2. Run:           python aeropad_receiver.py
  3. It prints a 6-digit PIN and advertises itself on the network (mDNS).
  4. In AeroPad: WiFi Control -> your PC appears -> Connect -> enter PIN.

Input injection uses pynput (keyboard+mouse). Gamepad messages are logged
only unless you install vgamepad (Windows) - flagged honestly below.
"""
import hashlib, json, random, socket, threading, time

PORT = 48653
PIN = str(random.randint(100000, 999999))

try:
    from pynput.keyboard import Controller as KC, Key
    from pynput.mouse import Controller as MC, Button
    kbd, mouse = KC(), MC()
    HAVE_PYNPUT = True
except ImportError:
    HAVE_PYNPUT = False
    print("WARNING: pynput missing - input will be LOGGED, not injected.")
    print("         pip install pynput")

try:
    from zeroconf import Zeroconf, ServiceInfo
    HAVE_ZEROCONF = True
except ImportError:
    HAVE_ZEROCONF = False
    print("NOTE: zeroconf missing - auto-discovery off (manual IP still works).")

def sha256(s):
    return hashlib.sha256(s.encode()).hexdigest()

SPECIALS = {
    40: "enter", 41: "esc", 42: "backspace", 43: "tab", 44: "space",
    79: "right", 80: "left", 81: "down", 82: "up",
}

def hid_to_char(code, mods):
    # HID usage 4-29 = a-z, 30-39 = 1-9,0
    if 4 <= code <= 29:
        c = chr(ord('a') + code - 4)
        return c.upper() if mods & 0x22 else c
    if 30 <= code <= 38:
        return chr(ord('1') + code - 30)
    if code == 39:
        return '0'
    return None

def handle(msg, out):
    t = msg.get("t")
    if t == "ping":
        out.write(json.dumps({"t": "pong", "echo": msg.get("echo")}) + "\n")
        out.flush()
        return
    if not HAVE_PYNPUT:
        print("MSG:", msg)
        return
    if t == "txt":
        kbd.type(msg.get("text", ""))
    elif t == "kd":
        code = msg.get("key", 0)
        ch = hid_to_char(code, msg.get("mods", 0))
        if ch:
            kbd.press(ch); kbd.release(ch)
        elif code in SPECIALS:
            k = getattr(Key, SPECIALS[code])
            kbd.press(k); kbd.release(k)
    elif t == "mm":
        mouse.move(msg.get("dx", 0), msg.get("dy", 0))
    elif t in ("mc", "md", "mu"):
        b = {1: Button.left, 2: Button.right, 4: Button.middle}.get(msg.get("btn", 1), Button.left)
        if t == "mc":
            mouse.click(b)
        elif t == "md":
            mouse.press(b)
        else:
            mouse.release(b)
    elif t == "ms":
        mouse.scroll(0, msg.get("scroll", 0))
    elif t == "gp":
        # Honest note: virtual gamepad needs vgamepad (Windows) or uinput
        # (Linux root). Without it we log the state only.
        print("GAMEPAD:", msg.get("buttons"), msg.get("hat"),
              msg.get("lx"), msg.get("ly"), msg.get("rx"), msg.get("ry"))

def client_thread(conn, addr):
    print(f"[+] {addr[0]} connected")
    try:
        rf = conn.makefile("r")
        wf = conn.makefile("w")
        nonce = str(random.randint(10**8, 10**9))
        wf.write(json.dumps({"t": "welcome", "text": nonce, "v": 1}) + "\n")
        wf.flush()
        hello = json.loads(rf.readline())
        if hello.get("proof") != sha256(PIN + ":" + nonce):
            wf.write(json.dumps({"t": "err", "text": "bad pin"}) + "\n")
            wf.flush()
            print(f"[-] {addr[0]} wrong PIN")
            return
        wf.write(json.dumps({"t": "ok", "deviceName": socket.gethostname()}) + "\n")
        wf.flush()
        print(f"[+] {addr[0]} authenticated ({hello.get('deviceName')})")
        for line in rf:
            try:
                handle(json.loads(line), wf)
            except Exception as e:
                print("bad msg:", e)
    except Exception as e:
        print(f"[-] {addr[0]} error: {e}")
    finally:
        conn.close()
        print(f"[-] {addr[0]} disconnected")

def local_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(("8.8.8.8", 80))
        return s.getsockname()[0]
    except Exception:
        return "127.0.0.1"
    finally:
        s.close()

def main():
    ip = local_ip()
    print("=" * 46)
    print("  AeroPad receiver")
    print(f"  Address : {ip}:{PORT}")
    print(f"  PIN     : {PIN}")
    print(f"  QR      : aeropad://{ip}:{PORT}/{PIN}")
    print("=" * 46)
    if HAVE_ZEROCONF:
        zc = Zeroconf()
        info = ServiceInfo(
            "_aeropad._tcp.local.",
            f"{socket.gethostname()}._aeropad._tcp.local.",
            addresses=[socket.inet_aton(ip)], port=PORT)
        zc.register_service(info)
        print("mDNS: advertising as _aeropad._tcp")
    srv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    srv.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    srv.bind(("0.0.0.0", PORT))
    srv.listen(2)
    while True:
        conn, addr = srv.accept()
        conn.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
        threading.Thread(target=client_thread, args=(conn, addr), daemon=True).start()

if __name__ == "__main__":
    main()
