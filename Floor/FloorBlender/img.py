import cv2
import numpy as np

from . import calc
from . import consts


def cv2_img(img, factor):
    return cv2.resize(img, None, fx=factor, fy=factor)


def calculate(preferred: float, value: float):
    return preferred / value


def noise(img):
    return cv2.fastNlMeansDenoisingColored(
        img,
        None,
        consts.img_H,
        consts.img_HCOLOR,
        consts.img_TEMPLATE_SIZE,
        consts.img_SEARCH_SIZE,
    )


def remove_noise(img, noise_removal_threshold):
    img[img < 128] = 0
    img[img > 128] = 255
    contours, _ = cv2.findContours(~img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    mask = np.zeros_like(img)
    for contour in contours:
        area = cv2.contourArea(contour)
        if area > noise_removal_threshold:
            cv2.fillPoly(mask, [contour], 255)
    return mask


def mark_black(img, mask):
    contours, _ = cv2.findContours(~img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    contour_sizes = [(cv2.contourArea(contour), contour) for contour in contours]
    biggest_contour = max(contour_sizes, key=lambda x: x[0])[1]
    mask = np.zeros_like(mask)
    cv2.fillPoly(mask, [biggest_contour], 255)
    img[mask == 0] = 0
    return img, mask


def detect_wall(reference_size, img):
    img_wall_size = calc.wall_wight(img)
    if img_wall_size is None:
        return None
    return calculate(float(reference_size), img_wall_size)
