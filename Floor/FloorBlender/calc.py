import cv2
import math
import numpy as np
from . import consts


def remove_walls_not_contour(walls, contour):
    res = []
    for wall in walls:
        for point in wall:
            if pic(point, contour):
                res.append(wall)
                break
    return res


def wall_wight(img):
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    height, width, channels = img.shape
    blank_img = np.zeros(
        (height, width, 3), np.uint8
    )

    wall_img = detect.wall_filter(gray)

    boxes, img = detect.p_boxes(wall_img, blank_img)

    filtered_boxes = list()
    for box in boxes:
        if len(box) == 4:
            x, y, w, h = cv2.boundingRect(box)
            if w > h:
                shortest = h
            else:
                shortest = w
            filtered_boxes.append(shortest)

    if (
        len(filtered_boxes) == 0
    ):
        return None

    return sum(filtered_boxes) / len(filtered_boxes)


def match_module(match_list):
    index1 = 0
    index2 = 0
    best = math.inf

    for i, _ in enumerate(match_list):
        for j, _ in enumerate(match_list):

            pos1_model = match_list[i][0]
            pos2_model = match_list[j][0]

            pos1_cap = match_list[i][1]
            pos2_cap = match_list[j][1]

            pt1 = (pos1_model[0] - pos2_model[0], pos1_model[1] - pos2_model[1])
            pt2 = (pos1_cap[0] - pos2_cap[0], pos1_cap[1] - pos2_cap[1])

            if pt1 == pt2 or pt1 == (0, 0) or pt2 == (0, 0):
                continue

            ang = math.degrees(andgle_2d(pt1, pt2))
            diff = ang % consts.STEP

            if diff < best:
                best = diff
                index1 = i
                index2 = j

    return index1, index2


def points_in_box(door, box):
    for point in door:
        if alm_contain_point(point, box):
            return True
    return False


def andgle_2d(vector1, vector2):
    x1, y1 = vector1
    x2, y2 = vector2
    inner_product = x1 * x2 + y1 * y2
    len1 = math.hypot(x1, y1)
    len2 = math.hypot(x2, y2)
    return math.acos(inner_product / (len1 * len2))


def box_center(box):
    x, y, w, h = cv2.boundingRect(box)
    return (x + w / 2, y + h / 2)


def dist_2d(p1, p2):
    return math.sqrt(abs(math.pow(p1[0] - p2[0], 2) - math.pow(p1[1] - p2[1], 2)))


def mag_2d(point):
    return math.sqrt(point[0] * point[0] + point[1] * point[1])


def norm_2d(normal):
    mag = mag_2d(normal)
    for i, val in enumerate(normal):
        normal[i] = val / mag
    return normal

def pic(points, contour):
    for x, y in points:
        if cv2.pointPolygonTest(contour, (x, y), False) == 1.0:
            return True
    return False

def alm_contain_point(pt, box):
    x, y, w, h = cv2.boundingRect(box)
    is_inside = x < pt[0] < x + w and y < pt[1] < y + h

    almost_inside = False

    min_dist = 0
    if w < h:
        min_dist = w
    else:
        min_dist = h

    for point in box:
        dist = abs(point[0][0] - pt[0]) + abs(point[0][1] - pt[1])
        if dist <= min_dist:
            almost_inside = True
            break

    return is_inside or almost_inside