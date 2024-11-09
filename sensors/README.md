# Bluetooth Proximity Sensing
## Set Up Instructions
1. connect to Raspberry Pi via ssh
2. clone this directory
3. install dependencies
```bash
sudo apt install libgirepository1.0-dev libdbus-1-dev libcairo2-dev gobject-introspection libgl1 python3-dbus bluez
```
4. set up python virtual environment
```bash
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```
5. copy contents of `template.service`
6. initialise systemd service by pasting copied contents into
```bash
sudo nano /etc/systemd/system/ble-proximity.service
```
7. start systemd service
```bash
sudo systemctl enable ble_proximity.service
sudo systemctl start ble_proximity.service
```
8. check status of systemd service
```bash
sudo systemctl status ble_proximity.service
```

## Usage Instructions
1. Pair your mobile device to the Raspberry Pi
2. Observe log files to verify actions

