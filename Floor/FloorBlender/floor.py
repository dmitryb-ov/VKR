import json

from . import configuration
from . import consts


def new_floorplan(config):
    return floor(config)


class floor:

    def __init__(self, conf=None):
        if conf is None:
            conf = consts.img_DEFAULT_CONFIG_FILE_NAME
        self.conf = conf
        self.create(self.conf)

    def __str__(self):
        return str(vars(self))

    def create(self, conf):
        settings = config.get(conf)
        settings_dict = {s: dict(settings.items(s)) for s in settings.sectutilns()}
        for group in settings_dict.items():
            for item in group[1].items():
                setattr(self, item[0], json.loads(item[1]))
