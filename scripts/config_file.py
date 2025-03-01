import configparser
from pathlib import Path


class ReadConfigClass(object):
    subjects_list = None
    def __new__(cls):
        cls.config=cls.read_conf()
        if not hasattr(cls, 'instance'):
            cls.instance = super(ReadConfigClass, cls).__new__(cls)
        return cls.instance

    @classmethod

    def read_conf(cls):
        config_dict = {}
        config = configparser.ConfigParser()
        config.read('./config_data.ini')
        for section in config.sections():
            home_dir = Path.home()
            for key in config[section]:
                val = config[section][key]
                if 'home/ali/' in val:
                    # print("Found home/ali/ in ",val)
                    val = val.replace('/home/ali/', str(home_dir) + '/')
                    val.replace("//","/")
                config_dict[key] = val
        return config_dict




# new_singleton = SingletonClass()
#
# print(singleton is new_singleton)
#
# singleton.singl_variable = "Singleton Variable"
# print(new_singleton.singl_variable)