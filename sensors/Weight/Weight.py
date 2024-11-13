import time
import sys
import RPi.GPIO as GPIO
# import requests
from hx711 import HX711

def cleanAndExit():
    print("Cleaning...")
        
    print("Bye!")
    sys.exit()

hx = HX711(5, 6)

'''
I've found out that, for some reason, the order of the bytes is not always the same between versions of python,
and the hx711 itself. I still need to figure out why.

If you're experiencing super random values, change these values to MSB or LSB until you get more stable values.
There is some code below to debug and log the order of the bits and the bytes.

The first parameter is the order in which the bytes are used to build the "long" value. The second paramter is
the order of the bits inside each byte. According to the HX711 Datasheet, the second parameter is MSB so you
shouldn't need to modify it.
'''
hx.set_reading_format("MSB", "MSB")

'''
# HOW TO CALCULATE THE REFFERENCE UNIT
1. Set the reference unit to 1 and make sure the offset value is set.
2. Load you sensor with 1kg or with anything you know exactly how much it weights.
3. Write down the 'long' value you're getting. Make sure you're getting somewhat consistent values.
    - This values might be in the order of millions, varying by hundreds or thousands and it's ok.
4. To get the wright in grams, calculate the reference unit using the following formula:
        
    referenceUnit = longValueWithOffset / 1000
        
In my case, the longValueWithOffset was around 114000 so my reference unit is 114,
because if I used the 114000, I'd be getting milligrams instead of grams.
'''

referenceUnit = -441
hx.set_reference_unit(referenceUnit)

hx.reset()

hx.tare()

print("Tare done! Add weight now...")

# to use both channels, you'll need to tare them both
#hx.tare_A()
#hx.tare_B()
previous_val = 0
# api call
# while True:
#     try:
#         # Get the current weight
#         val = hx.get_weight(5)
#         print("Current value:", val)

#         # Check if the value increased by 50% or more
#         if previous_val != 0 and val >= previous_val * 1.5:
#             # Send API call with "Delivery Received" header
#             headers = {"Status": "Delivery Received"}
#             response = requests.post("http://<your_ip_address>:5000/weight_event", headers=headers)
#             print("API called with header 'Delivery Received', Response:", response.status_code)

#         # Check if the value dropped by 50% or more, or is near 0/negative
#         elif val <= previous_val * 0.5 or val <= 0:
#             # Send API call with "Parcel Taken" header
#             headers = {"Status": "Parcel Taken"}
#             response = requests.post("http://<your_ip_address>:5000/weight_event", headers=headers)
#             print("API called with header 'Parcel Taken', Response:", response.status_code)

#         # Update previous value
#         previous_val = val

#         # Reset the sensor and wait before the next reading
#         hx.power_down()
#         hx.power_up()
#         time.sleep(0.1)

#     except (KeyboardInterrupt, SystemExit):
#         cleanAndExit()

previous_val = 0

while True:
    try:
        # Get the current weight
        val = hx.get_weight(5)
        print("Current value:", val)

        # Check if the value increased by 50% or more
        if previous_val != 0 and val >= previous_val * 1.5:
            # Print message for "Delivery Received"
            print("Delivery Received: Value increased by 50% or more.")

        # Check if the value dropped by 50% or more, or is near 0/negative
        elif val <= previous_val * 0.5 or val <= 0:
            # Print message for "Parcel Taken"
            print("Parcel Taken: Value dropped by 50% or is near 0 or negative.")

        # Update previous value
        previous_val = val

        # Reset the sensor and wait before the next reading
        hx.power_down()
        hx.power_up()
        time.sleep(0.1)

    except (KeyboardInterrupt, SystemExit):
        cleanAndExit()