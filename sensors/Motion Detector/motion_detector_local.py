import cv2
import numpy as np
import time

# Initialize the video capture from the stream URL
stream_url = "http://127.0.1.1:8089/?action=stream"
cap = cv2.VideoCapture(stream_url)

# Initialize variables for motion detection
motion_start_time = None
motion_duration_threshold = 0.5  # seconds
motion_sensitivity = 500  # Adjust based on the level of motion you consider significant

# Frame setup
_, prev_frame = cap.read()
prev_frame_gray = cv2.cvtColor(prev_frame, cv2.COLOR_BGR2GRAY)
prev_frame_gray = cv2.GaussianBlur(prev_frame_gray, (21, 21), 0)

while True:
    # Capture current frame
    _, frame = cap.read()
    if frame is None:
        break

    # Convert to grayscale and apply Gaussian blur
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (21, 21), 0)

    # Compute the absolute difference between the current and previous frames
    frame_delta = cv2.absdiff(prev_frame_gray, gray)
    thresh = cv2.threshold(frame_delta, 25, 255, cv2.THRESH_BINARY)[1]

    # Dilate the thresholded image to fill in holes, then find contours
    thresh = cv2.dilate(thresh, None, iterations=2)

    # Handle findContours output for compatibility with OpenCV versions
    contour_data = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    contours = contour_data[0] if len(contour_data) == 2 else contour_data[1]

    # Calculate motion by checking the area of contours
    motion_detected = False
    for contour in contours:
        if cv2.contourArea(contour) > motion_sensitivity:
            motion_detected = True
            break

    # Check if motion has been detected
    if motion_detected:
        if motion_start_time is None:
            motion_start_time = time.time()
        elif time.time() - motion_start_time >= motion_duration_threshold:
            # Take a picture when motion lasts for more than threshold
            timestamp = time.strftime("%Y%m%d-%H%M%S")
            image_path = f"motion_capture_{timestamp}.jpg"
            cv2.imwrite(image_path, frame)
            print(f"Motion detected. Image saved as {image_path}")
            motion_start_time = None  # Reset the timer after taking a picture
    else:
        motion_start_time = None  # Reset if motion stops before duration threshold

    # Update the previous frame
    prev_frame_gray = gray

    # Optional: Display the frame and threshold for debugging purposes
    cv2.imshow("Live Stream", frame)
    cv2.imshow("Threshold", thresh)

    # Press 'q' to exit
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release resources
cap.release()
cv2.destroyAllWindows()
