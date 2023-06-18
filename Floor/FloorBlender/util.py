import json
import os
import platform
from sys import platform as pf

import cv2

from . import configuration
from . import consts
from . import img


def blender_installed():
    if pf == "linux" or pf == "linux2":
        return find_files("blender", "/")
    elif pf == "darwin":
        return find_files("blender", "/")
    elif pf == "win32":
        return find_files("blender.exe", "C:\\")


def get_blender_os_path():
    _platform = platform.system()
    if (
        _platform.lower() == "linux"
        or _platform.lower() == "linux2"
        or _platform.lower() == "ubuntu"
    ):
        return consts.LINUX_DEFAULT_BLENDER_INSTALL_PATH
    elif _platform.lower() == "darwin":
        return consts.MAC_DEFAULT_BLENDER_INSTALL_PATH
    elif "win" in _platform.lower():
        return consts.WIN_DEFAULT_BLENDER_INSTALL_PATH


def read_img(path, floorplan=None):
    img = cv2.imread(path)
    if img is None:
        raise utilError

    scale_factor = 1
    if floorplan is not None:
        if floorplan.remove_noise:
            img = img.noise(img)
        if floorplan.rescale_img:

            calibratutilns = config.convert_img(floorplan)
            floorplan.wall_size_calibratutiln = calibratutilns
            scale_factor = img.detect_wall(float(calibratutilns), img)
            if scale_factor is None:
                scale_factor = 1
            else:
                img = img.cv2_img(img, scale_factor)

    return img, cv2.cvtColor(img, cv2.COLOR_BGR2GRAY), scale_factor


def save_to_file(file_path, data, show=True):
    with open(file_path + consts.SAVE_DATA_FORMAT, "w") as f:
        try:
            f.write(json.dumps(data))
        except TypeError:
            f.write(json.dumps(data, default=ndarrayJsonDumps))