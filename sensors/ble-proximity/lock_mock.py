import os
import logging
import RPi.GPIO as GPIO

# relay_pin = 23
relay_pin = 18

# Set up logger
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
log_file_path = os.path.join(os.path.dirname(__file__), "logs/lock.log")
file_handler = logging.FileHandler(log_file_path)
file_handler.setLevel(logging.INFO)
formatter = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s")
file_handler.setFormatter(formatter)
logger.addHandler(file_handler)

GPIO.setmode(GPIO.BCM)
GPIO.setup(relay_pin, GPIO.IN)

def open_lock():
    GPIO.setup(relay_pin, GPIO.OUT, initial=GPIO.HIGH)
    # GPIO.output(relay_pin, GPIO.HIGH)
    GPIO.output(relay_pin, False)
    print("lock opening")
    logger.info("lock opening")

def close_lock():
    GPIO.setup(relay_pin, GPIO.IN)
    GPIO.input(relay_pin)
    # GPIO.output(relay_pin, True)
    print("lock closeing")
    logger.info("lock closeing")
