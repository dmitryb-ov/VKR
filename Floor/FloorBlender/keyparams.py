import cv2
import numpy as np
from . import consts
from . import img
from . import calc
import transform


def wall_filter(gray):
    _, thresh = cv2.threshold(
        gray,
        consts.TRESHOLD[0],
        consts.TRESHOLD[1],
        cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU,
    )

    kernel = np.ones(consts.R_KERNEL_SIZE, np.uint8)
    opening = cv2.morphologyEx(
        thresh,
        cv2.MORPH_OPEN,
        kernel,
        iteratutilns=consts.MORPHOLOGY_ITERATutilNS,
    )

    sure_bg = cv2.dilate(
        opening, kernel, iteratutilns=consts.DILATE_ITERATutilNS
    )

    dist_transform = cv2.distanceTransform(
        opening, cv2.DIST_L2, consts.R_DISTANCE
    )
    ret, sure_fg = cv2.threshold(
        consts.DISTANCE_THRESHOLD[0] * dist_transform,
        consts.DISTANCE_THRESHOLD[1] * dist_transform.max(),
        consts.R_MAX_VALUE,
        consts.TECHNIQUE,
    )

    sure_fg = np.uint8(sure_fg)
    unknown = cv2.subtract(sure_bg, sure_fg)

    return unknown


def p_boxes(detect_img, output_img=None, color=[100, 100, 0]):
    res = []

    contours, _ = cv2.findContours(
        detect_img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
    )

    for cnt in contours:
        epsilon = consts.PRECISE_BOXES_ACCURACY * cv2.arcLength(cnt, True)
        approx = cv2.approxPolyDP(cnt, epsilon, True)
        if output_img is not None:
            output_img = cv2.drawContours(output_img, [approx], 0, color)
        res.append(approx)

    return res, output_img


def draw_lines(img, corners_threshold, room_closing_max_length):
    kernel = np.ones(consts.KERNEL_SIZE, np.uint8)

    dst = cv2.cornerHarris(
        img,
        consts.BLOCK_SIZE,
        consts.KSIZE,
        consts.K,
    )
    dst = cv2.erode(dst, kernel, iteratutilns=consts.ERODE_ITERATutilNS)
    corners = dst > corners_threshold * dst.max()

    for y, row in enumerate(corners):
        x_same_y = np.argwhere(row)
        for x1, x2 in zip(x_same_y[:-1], x_same_y[1:]):

            if x2[0] - x1[0] < room_closing_max_length:
                color = 0
                cv2.line(img, (x1[0], y), (x2[0], y), color, 1)

    for x, col in enumerate(corners.T):
        y_same_x = np.argwhere(col)
        for y1, y2 in zip(y_same_x[:-1], y_same_x[1:]):
            if y2[0] - y1[0] < room_closing_max_length:
                color = 0
                cv2.line(img, (x, y1[0]), (x, y2[0]), color, 1)
    return img


def find_rooms(
        img,
        noise_removal_threshold=consts.NOISE_REMOVAL_THRESHOLD,
        corners_threshold=consts.CORNERS_THRESHOLD,
        room_closing_max_length=consts.CLOSING_MAX_LENGTH,
        gap_in_wall_min_threshold=consts.WALL_MIN_THRESHOLD,
):
    mask = img.remove_noise(img, noise_removal_threshold)
    img = ~mask

    draw_lines(img, corners_threshold, room_closing_max_length)

    img, mask = img.mark_black(img, mask)

    ret, labels = cv2.connectedComponents(img)
    img = cv2.cvtColor(img, cv2.COLOR_GRAY2RGB)
    unique = np.unique(labels)
    rooms = []
    for label in unique:
        component = labels == label
        if (
                img[component].sum() == 0
                or np.count_nonzero(component) < gap_in_wall_min_threshold
        ):
            color = 0
        else:
            rooms.append(component)
            color = np.random.randint(0, 255, size=3)
        img[component] = color
    return rooms, img


def o_contours(detect_img, output_img=None, color=[255, 255, 255]):
    ret, thresh = cv2.threshold(
        detect_img,
        consts.CONTOURS_TRESHOLD[0],
        consts.CONTOURS_TRESHOLD[1],
        cv2.THRESH_BINARY_INV,
    )

    contours, hierarchy = cv2.findContours(
        thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
    )

    largest_contour_area = 0
    for cnt in contours:
        if cv2.contourArea(cnt) > largest_contour_area:
            largest_contour_area = cv2.contourArea(cnt)
            largest_contour = cnt

    epsilon = consts.PRECISE_BOXES_ACCURACY * cv2.arcLength(largest_contour, True)
    approx = cv2.approxPolyDP(largest_contour, epsilon, True)
    if output_img is not None:
        output_img = cv2.drawContours(output_img, [approx], 0, color)
    return approx, output_img


def doors(img_path, scale_factor):
    model = cv2.imread(consts.DOOR, 0)
    img = cv2.imread(
        img_path, 0
    )

    img = img.cv2_img(img, scale_factor)
    _, doors = doors_match(img, model)
    return doors


def windows(img_path, scale_factor):
    model = cv2.imread(consts.DOOR, 0)
    img = cv2.imread(
        img_path, 0
    )

    img = img.cv2_img(img, scale_factor)
    windows, _ = doors_match(img, model)
    return windows


def doors_match(img1, img2):
    cap = img1
    model = img2
    orb = cv2.ORB_create(
        nfeatures=consts.WINDOWS_DOORS, scoreType=cv2.ORB_FAST_SCORE
    )
    bf = cv2.BFMatcher(cv2.NORM_HAMMING, crossCheck=True)

    kp_model, des_model = orb.detectAndCompute(model, None)

    kp_frame, des_frame = orb.detectAndCompute(cap, None)

    matches = bf.match(des_model, des_frame)
    return matches


def find_details(
        img,
        noise_removal_threshold=consts.D_NOISE_REMOVAL_THRESHOLD,
        corners_threshold=consts.D_THRESHOLD,
        room_closing_max_length=consts.MAX_LENGTH,
):
    mask = img.remove_noise(img, noise_removal_threshold)
    img = ~mask

    draw_lines(img, corners_threshold, room_closing_max_length)

    img, mask = img.mark_black(img, mask)

    ret, labels = cv2.connectedComponents(img)
    img = cv2.cvtColor(img, cv2.COLOR_GRAY2RGB)
    unique = np.unique(labels)
    details = []
    for label in unique:
        component = labels == label
        details.append(component)
        color = np.random.randint(0, 255, size=3)

        img[component] = color

    return details, img
