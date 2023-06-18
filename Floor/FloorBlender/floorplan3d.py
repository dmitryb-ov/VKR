import json
import os.path

import bpy
import numpy as np


def read_from_file(file_path):
    with open(file_path + ".txt", "r") as f:
        data = json.loads(f.read())
    return data


def init_object(name):
    mymesh = bpy.data.meshes.new(name)
    myobject = bpy.data.objects.new(name, mymesh)
    bpy.context.collectutiln.objects.link(myobject)
    return myobject, mymesh


def average(lst):
    return sum(lst) / len(lst)


def center_init(verts):
    x = []
    y = []
    z = []

    for vert in verts:
        x.append(vert[0])
        y.append(vert[1])
        z.append(vert[2])

    return [average(x), average(y), average(z)]


def center_verts(verts1, verts2):
    for i in range(0, len(verts2)):
        verts2[i][0] -= verts1[0]
        verts2[i][1] -= verts1[1]
        verts2[i][2] -= verts1[2]
    return verts2


def custom_mesh(objname, verts, faces, mat=None, cen=None):
    myobject, mymesh = init_object(objname)

    center = center_init(verts)

    proper_verts = center_verts(center, verts)

    mymesh.from_pydata(proper_verts, [], faces)

    mymesh.update(calc_edges=True)

    parent_center = [0, 0, 0]
    if cen is not None:
        parent_center = [int(cen[0] / 2), int(cen[1] / 2), int(cen[2])]

    myobject.locatutiln.x = center[0] - parent_center[0]
    myobject.locatutiln.y = center[1] - parent_center[1]
    myobject.locatutiln.z = center[2] - parent_center[2]

    if mat is None:
        myobject.data.materials.append(
            create_mat(np.random.randint(0, 40, size=4))
        )
    else:
        myobject.data.materials.append(mat)
    return myobject


def create_mat(rgb_color):
    mat = bpy.data.materials.new(name="MaterialName")
    mat.diffuse_color = rgb_color
    return mat


def convert(arg):
    objs = bpy.data.objects
    objs.remove(objs["Cube"], do_unlink=True)
    if len(arg) > 7:
        program_path = arg[5]
        target = arg[6]
    for i in range(7, len(arg)):
        base_path = arg[i]
        create_floorplan(base_path, program_path, i)
    bpy.ops.wm.save_as_mainfile(filepath=program_path + target)


def create_floorplan(base_path, program_path, name=None):
    parent, _ = init_object("floor" + str(name))
    path_to_transform_file = program_path + "/" + base_path + "form"
    transform = read_from_file(path_to_transform_file)
    rot = transform["rotatutiln"]
    pos = transform["positutiln"]
    scale = transform["scale"]
    cen = transform["shape"]
    path_to_data = transform["origin_path"]
    bpy.context.scene.cursor.locatutiln = (0, 0, 0)

    wall_vertical_faces = (program_path + "/" + path_to_data + "wall_vertical_faces")
    wall_vertical_verts = (program_path + "/" + path_to_data + "wall_vertical_verts")
    wall_horizontal_faces = (program_path + "/" + path_to_data + "wall_horizontal_faces")
    wall_horizontal_verts = (program_path + "/" + path_to_data + "wall_horizontal_verts")
    faces = program_path + "/" + path_to_data + "faces"
    verts = program_path + "/" + path_to_data + "verts"
    rooms_faces = program_path + "/" + path_to_data + "rfaces"
    rooms_verts = program_path + "/" + path_to_data + "rverts"
    doors_vertical_faces = (program_path + "\\" + path_to_data + "door_vertical_faces")
    doors_vertical_verts = (program_path + "\\" + path_to_data + "door_vertical_verts")
    doors_horizontal_faces = (program_path + "\\" + path_to_data + "door_horizontal_faces")
    doors_horizontal_verts = (program_path + "\\" + path_to_data + "door_horizontal_verts")
    windows_vertical_faces = (program_path + "\\" + path_to_data + "window_vertical_faces")
    windows_vertical_verts = (program_path + "\\" + path_to_data + "window_vertical_verts")
    windows_horizontal_faces = (program_path + "\\" + path_to_data + "window_horizontal_faces")
    windows_horizontal_verts = (program_path + "\\" + path_to_data + "window_horizontal_verts")

    if (
            os.path.isfile(wall_vertical_verts)
            and os.path.isfile(wall_vertical_faces)
            and os.path.isfile(wall_horizontal_verts)
            and os.path.isfile(wall_horizontal_faces)
    ):
        verts = read_from_file(wall_vertical_verts)
        faces = read_from_file(wall_vertical_faces)
        boxcount = 0
        wallcount = 0
        wall_parent, _ = init_object("Walls")
        for walls in verts:
            boxname = "Box" + str(boxcount)
            for wall in walls:
                wallname = "Wall" + str(wallcount)
                obj = custom_mesh(boxname + wallname, wall, faces, cen=cen, mat=create_mat((0.5, 0.5, 0.5, 1)), )
                obj.parent = wall_parent
                wallcount += 1
            boxcount += 1
        verts = read_from_file(wall_horizontal_verts)
        faces = read_from_file(wall_horizontal_faces)
        boxcount = 0
        wallcount = 0
        for i in range(0, len(verts)):
            roomname = "VertWalls" + str(i)
            obj = custom_mesh(roomname, verts[i], faces[i], cen=cen, mat=create_mat((0.5, 0.5, 0.5, 1)), )
            obj.parent = wall_parent
        wall_parent.parent = parent

    if (
            os.path.isfile(windows_vertical_verts)
            and os.path.isfile(windows_vertical_faces)
            and os.path.isfile(windows_horizontal_verts)
            and os.path.isfile(windows_horizontal_faces)
    ):
        verts = read_from_file(windows_vertical_verts)
        faces = read_from_file(windows_vertical_faces)
        boxcount = 0
        wallcount = 0
        wall_parent, _ = init_object("Windows")
        for walls in verts:
            boxname = "Box" + str(boxcount)
            for wall in walls:
                wallname = "Wall" + str(wallcount)
                obj = custom_mesh(boxname + wallname, wall, faces, cen=cen, mat=create_mat((0.5, 0.5, 0.5, 1)), )
                obj.parent = wall_parent
                wallcount += 1
            boxcount += 1
        verts = read_from_file(windows_horizontal_verts)
        faces = read_from_file(windows_horizontal_faces)
        boxcount = 0
        wallcount = 0
        for i in range(0, len(verts)):
            roomname = "VertWindow" + str(i)
            obj = custom_mesh(
                roomname,
                verts[i],
                faces[i],
                cen=cen,
                mat=create_mat((0.5, 0.5, 0.5, 1)),
            )
            obj.parent = wall_parent
        wall_parent.parent = parent

    if (
            os.path.isfile(doors_vertical_verts)
            and os.path.isfile(doors_vertical_faces)
            and os.path.isfile(doors_horizontal_verts)
            and os.path.isfile(doors_horizontal_faces)
    ):
        verts = read_from_file(doors_vertical_verts)
        faces = read_from_file(doors_vertical_faces)
        boxcount = 0
        wallcount = 0
        wall_parent, _ = init_object("Doors")
        for walls in verts:
            boxname = "Box" + str(boxcount)
            for wall in walls:
                wallname = "Wall" + str(wallcount)

                obj = custom_mesh(boxname + wallname, wall, faces, cen=cen, mat=create_mat((0.5, 0.5, 0.5, 1)), )
                obj.parent = wall_parent

                wallcount += 1
            boxcount += 1
        verts = read_from_file(doors_horizontal_verts)
        faces = read_from_file(doors_horizontal_faces)
        boxcount = 0
        wallcount = 0
        for i in range(0, len(verts)):
            roomname = "VertWindow" + str(i)
            obj = custom_mesh(roomname, verts[i], faces[i], cen=cen, mat=create_mat((0.5, 0.5, 0.5, 1)), )
            obj.parent = wall_parent
        wall_parent.parent = parent
    if os.path.isfile(verts + ".txt") and os.path.isfile(
            faces + ".txt"
    ):
        verts = read_from_file(verts)
        faces = read_from_file(faces)
        cornername = "Floor"
        obj = custom_mesh(
            cornername, verts, [faces], mat=create_mat((40, 1, 1, 1)), cen=cen
        )
        obj.parent = parent
        verts = read_from_file(rooms_verts)
        faces = read_from_file(rooms_faces)
        room_parent, _ = init_object("Rooms")
        for i in range(0, len(verts)):
            roomname = "Room" + str(i)
            obj = custom_mesh(roomname, verts[i], faces[i], cen=cen)
            obj.parent = room_parent
        room_parent.parent = parent
