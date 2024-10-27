import asyncio
from bleak import BleakScanner
import os
import time
import logging
import lock_mock
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

RSSI_THRESHOLD = -70  # Adjust based on your environment
devices = []

# Set up logger
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
log_file_path = os.path.join(os.path.dirname(__file__), "logs/discover.log")
file_handler = logging.FileHandler(log_file_path)
file_handler.setLevel(logging.INFO)
formatter = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s")
file_handler.setFormatter(formatter)
logger.addHandler(file_handler)

async def monitor_proximity(mac_addresses):
    logger.info(f"Monitoring proximity of devices: {mac_addresses}")
    print(f"Monitoring proximity of devices: {mac_addresses}")

    while True:
        devices = await BleakScanner.discover(timeout=0.5)
        device_found = False

        for device in devices:
            if device.address in mac_addresses:
                rssi = device.rssi
                logger.info(f"{device.address} RSSI: {rssi}")
                print(f"{device.address} RSSI: {rssi}")
                if rssi > RSSI_THRESHOLD:
                    logger.info("Device is nearby! Logging proximity.")
                    print("Device is nearby! Logging proximity.")
                    lock_mock.open_lock()
                else:
                    logger.info("Device is too far away.")
                    print("Device is too far away.")
                    lock_mock.close_lock()
                device_found = True
                break

        if not device_found:
            logger.info("Device not found.")
            print("Device not found.")
            lock_mock.close_lock()

        time.sleep(0.5)

def get_device_mac():
    global devices
    if not os.path.exists("device_mac.txt"):
        logger.error("Device MAC address not found. Please pair your mobile device!")
        print("Device MAC address not found. Please pair your mobile device!")
        return devices
    while devices:
        devices.pop()
    with open("device_mac.txt", "r") as input_file:
        for line in input_file:
            line = line.strip()
            devices.append(line)
    logger.info(f"Loaded devices: {devices}")
    print(f"Loaded devices: {devices}")
    return devices

class DeviceFileHandler(FileSystemEventHandler):
    """Handler for file system events related to device_mac.txt."""

    def on_modified(self, event):
        """Called when the file is modified."""
        if event.src_path.endswith("device_mac.txt"):
            print("device_mac.txt has been modified, reloading devices...")
            get_device_mac()

if __name__ == "__main__":
    get_device_mac()

    event_handler = DeviceFileHandler()
    observer = Observer()
    observer.schedule(event_handler, path=".", recursive=False)
    observer.start()

    try:
        asyncio.run(monitor_proximity(devices))
    except KeyboardInterrupt:
        logger.info("Monitoring stopped.")
        print("Monitoring stopped.")
    finally:
        observer.stop()
        observer.join()
