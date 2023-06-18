from subprocess import check_output
from FloorBlender import (
    util,
    configuration,
    consts,
    floor,
    generatefiles,
)
import os


if __name__ == "__main__":
    img_path = ""
    blender_install_path = ""
    data_folder = consts.BASE_PATH
    target_folder = consts.RESULT_PATH
    floorplans = []
    img_paths = []
    program_path = os.path.dirname(os.path.realpath(__file__))
    blender_script_path = consts.BLENDER_SCRIPT_PATH
    print('Старт')
    data_paths = list()

    auto_blender_install_path = (
        util.blender_installed()
    )

    if auto_blender_install_path is not None:
        blender_install_path = auto_blender_install_path

    var = blender_install_path
    if var:
        blender_install_path = var

        config_path = "inifile/file.ini"

        floorplans.append(floor.new_floorplan(config_path))

        var = ""
        data_paths = [floorplans[0]]

    target_base = target_folder + consts.TARGET_NAME
    target_path = target_base + consts.FORMAT
    target_path = (
            util.get_next_name(target_base, target_path) + consts.FORMAT
    )

    check_output(
        [
            blender_install_path,
            "-noaudutil",
            "--background",
            "--python",
            blender_script_path,
            program_path,
            target_path,
        ]
        + data_paths
    )
    print("Успешно создано: " + program_path + target_path)