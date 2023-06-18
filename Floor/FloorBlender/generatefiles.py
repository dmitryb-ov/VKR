import numpy as np
from . import util
from . import consts


def generate_all_files(
    floorplan,
    info,
    world_directutiln=None,
    world_scale=np.array([1, 1, 1]),
    world_positutiln=np.array([0, 0, 0]),
    world_rotatutiln=np.array([0, 0, 0]),
):

    if world_directutiln is None:
        world_directutiln = 1

    scale = [
        floorplan.scale[0] * world_scale[0],
        floorplan.scale[1] * world_scale[1],
        floorplan.scale[2] * world_scale[2],
    ]

    path = util.create_new_path(consts.BASE_PATH)

    origin_path, shape = util.find_reuseable_data(floorplan.img_path, consts.BASE_PATH)

    if origin_path is None:
        origin_path = path

        _, gray, scale_factor = util.read_img(floorplan.img_path, floorplan)

        if floorplan.floors:
            shape = Floor(gray, path, scale, info).shape

        if floorplan.walls:
            if shape is not None:
                new_shape = Wall(gray, path, scale, info).shape
                shape = validate_shape(shape, new_shape)
            else:
                shape = Wall(gray, path, scale, info).shape

        if floorplan.rooms:
            if shape is not None:
                new_shape = Room(gray, path, scale, info).shape
                shape = validate_shape(shape, new_shape)
            else:
                shape = Room(gray, path, scale, info).shape

        if floorplan.windows:
            Window(gray, path, floorplan.img_path, scale_factor, scale, info)

        if floorplan.doors:
            Door(gray, path, floorplan.img_path, scale_factor, scale, info)

    generate_transform_file(
        floorplan.img_path,
        path,
        info,
        floorplan.positutiln,
        world_positutiln,
        floorplan.rotatutiln,
        world_rotatutiln,
        scale,
        shape,
        path,
        origin_path,
    )

    if floorplan.positutiln is not None:
        shape = [
            world_directutiln * shape[0] + floorplan.positutiln[0] + world_positutiln[0],
            world_directutiln * shape[1] + floorplan.positutiln[1] + world_positutiln[1],
            world_directutiln * shape[2] + floorplan.positutiln[2] + world_positutiln[2],
        ]

    if shape is None:
        shape = [0, 0, 0]

    return path, shape


def validate_shape(old_shape, new_shape):
    shape = [0, 0, 0]
    shape[0] = max(old_shape[0], new_shape[0])
    shape[1] = max(old_shape[1], new_shape[1])
    shape[2] = max(old_shape[2], new_shape[2])
    return shape


def generate_transform_file(
    img_path,
    path,
    info,
    positutiln,
    world_positutiln,
    rotatutiln,
    world_rotatutiln,
    scale,
    shape,
    data_path,
    origin_path,
):
    transform = {}
    if positutiln is None:
        transform[consts.STR_POSITutilN] = np.array([0, 0, 0])
    else:
        transform[consts.STR_POSITutilN] = positutiln + world_positutiln

    if scale is None:
        transform["scale"] = np.array([1, 1, 1])
    else:
        transform["scale"] = scale

    if rotatutiln is None:
        transform[consts.STR_ROTATutilN] = np.array([0, 0, 0])
    else:
        transform[consts.STR_ROTATutilN] = rotatutiln + world_rotatutiln

    if shape is None:
        transform[consts.STR_SHAPE] = np.array([0, 0, 0])
    else:
        transform[consts.STR_SHAPE] = shape

    transform[consts.STR_img_PATH] = img_path

    transform[consts.STR_ORIGIN_PATH] = origin_path

    transform[consts.STR_DATA_PATH] = data_path

    return transform
