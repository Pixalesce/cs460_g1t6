import os
import logging
import RPi.GPIO as GPIO

relay_pin = 23

# Set up logger
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
log_file_path = os.path.join(os.path.dirname(__file__), "logs/lock.log")
file_handler = logging.FileHandler(log_file_path)
file_handler.setLevel(logging.INFO)
formatter = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s")
file_handler.setFormatter(formatter)
logger.addHandler(file_handler)

def open_lock():
    GPIO.output(relay_pin, GPIO.HIGH)
    print("lock opening")
    logger.info("lock opening")

def close_lock():
    GPIO.input(relay_pin)
    print("lock closeing")
    logger.info("lock closeing")
