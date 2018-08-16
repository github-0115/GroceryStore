#!/usr/bin/env python
#! _*_ coding:utf-8 _*_

"""
python access.py /opt/uyun [敏感文件名称，可不填],etc
给定安装目录和敏感文件的名字，自动扫描目录下所有文件的权限并进行判断，
敏感文件用逗号分割，也可以不填写
只针对linux平台
生成的日志文件在当前运行的目录，文件名为access.log
"""
import os
import sys
import logging.config

class store_all(object):
    """
    将扫描出的所有文件，文件夹进行存储
    """
    def __init__(self, sensitive_names):
        self.sn = sensitive_names
        self.all_folders = dict()  #存放所有文件夹
        self.all_xf = dict()   #存放所有可执行文件
        self.all_nxf = dict()  #存放所有不可执行文件
        self.all_log = dict()  #存放所有日志文件
        self.all_sn = dict()   #存放敏感文件

    def pushfile(self, files):
        """
        存放文件
        """
        if files.split(os.path.sep)[-1] in self.sn:
            self.all_sn[files] = oct(os.stat(files).st_mode)[-3:]
        if files[-4:] == ".log":
            self.all_log[files] = oct(os.stat(files).st_mode)[-3:]
        if os.access(files, os.X_OK):
            self.all_xf[files] = oct(os.stat(files).st_mode)[-3:]
        if not os.access(files, os.X_OK):
            self.all_nxf[files] = oct(os.stat(files).st_mode)[-3:]

    def pushfolder(self, folder):
        """
        存放文件夹
        """
        self.all_folders[folder] = oct(os.stat(folder).st_mode)[-3:]

logger_json = {
    "version": 1,
    "handlers": {
        "h1": {
            "class": "logging.StreamHandler",
            "level": "INFO",
            "stream": "ext://sys.stdout"
        },
        "h2": {
            "class": "logging.FileHandler",
            "level": "INFO",
            "filename": "access.log",
            "mode": "w"
        }
    },
    "root": {
        "level": "DEBUG",
        "handlers": ["h1", "h2"]
    }
}

def compare(chmod1, chmod2):
    """
    判断权限是否符合要求
    """
    for index in range(3):
        if int(chmod1[index]) > int(chmod2[index]):
            return False
    return True

def find_all(direction):
    """
    扫描给定目录下所有文件
    """
    all_files = os.walk(direction)
    for root, dis, name in all_files:
        for files in name:
            store.pushfile(root + os.path.sep + files)
        if dis:
            for dirf in dis:
                store.pushfolder(root + os.path.sep + dirf)
    store.pushfolder(direction)

def check_x(auth):
    """
    扫描可执行文件
    """
    logger.info("开始检查所有可执行文件…………")
    num = 0
    for files, chmods in store.all_xf.iteritems():
        if not compare(chmods, auth):
            os.chmod(files, int(auth, 8))
            num = num + 1
            tips = "可执行文件{document}的权限是{fact_auth}，大于要求的权限{require_auth}".format(
                document=files,
                fact_auth=chmods,
                require_auth=auth
            )
            logger.info(tips)
    if num:
        logger.info(
            "可执行文件权限检查完毕，共{num}个文件的权限大于{require_auth}\n".format(
                num=num,
                require_auth=auth
                )
            )
    else:
        logger.info("可执行文件权限检查完毕, 没有大于要求权限的文件\n")

def check_nx(auth):
    """
    扫描不可执行文件
    """
    logger.info("开始检查所有不可执行文件…………")
    num = 0
    for files, chmods in store.all_nxf.iteritems():
        if not compare(chmods, auth):
            os.chmod(files, int(auth, 8))
            num = num + 1
            tips = "不可执行文件{document}的权限是{fact_auth}，大于要求的权限{require_auth}".format(
                document=files,
                fact_auth=chmods,
                require_auth=auth
                )
            logger.info(tips)
    if num:
        logger.info(
            "不可执行文件权限检查完毕，共{num}个文件的权限大于{require_auth}\n".format(
                num=num,
                require_auth=auth
                )
            )
    else:
        logger.info("不可执行文件权限检查完毕, 没有大于要求权限的文件\n")

def check_log(auth):
    """
    扫描日志文件
    """
    logger.info("开始检查所有日志文件…………")
    num = 0
    for files, chmods in store.all_log.iteritems():
        if not compare(chmods, auth):
            os.chmod(files, int(auth, 8))
            num = num + 1
            tips = "日志文件{document}的权限是{fact_auth}，大于要求的权限{require_auth}".format(
                document=files,
                fact_auth=chmods,
                require_auth=auth
                )
            logger.info(tips)
    if num:
        logger.info(
            "日志文件检查完毕，共{num}个文件的权限大于{require_auth}\n".format(
                num=num,
                require_auth=auth
                )
            )
    else:
        logger.info("日志文件检查完毕，没有大于要求权限的文件\n")

def check_folder(auth):
    """
    扫描目录
    """
    logger.info("开始检查所有目录…………")
    num = 0
    for folder, chmods in store.all_folders.iteritems():
        if not compare(chmods, auth):
            os.chmod(folder, int(auth, 8))
            num = num + 1
            tips = "目录{document}的权限是{fact_auth}，大于要求的权限{require_auth}".format(
                document=folder,
                fact_auth=chmods,
                require_auth=auth
                )
            logger.info(tips)
    if num:
        logger.info(
            "目录检查完毕，共{num}个目录的权限大于{require_auth}\n".format(
                num=num,
                require_auth=auth
                )
            )
    else:
        logger.info("目录检查完毕，没有大于要求权限的文件\n")

def check_mingan(args, auth):
    """
    扫描敏感文件
    """
    logger.info("开始检查敏感文件…………")
    num = 0
    file_m = list()
    if not args:
        logger.info("未提供敏感文件，检查结束")
        return

    for files, chmods in store.all_sn.iteritems():
        if not compare(chmods, auth):
            os.chmod(files, int(auth, 8))
            num = num + 1
            file_m.append(files.split(os.path.sep)[-1])
            tips = "敏感文件{document}的权限是{fact_auth}，大于要求的权限{require_auth}".format(
                document=files,
                fact_auth=chmods,
                require_auth=auth
                )
            logger.info(tips)
    if not (set(args) - set(file_m)):
        if num:
            logger.info("敏感文件检查完毕，共{}个文件大于要求的权限".format(num))
        else:
            logger.info("敏感文件检查完毕，没有大于要求的权限的文件")
    else:
        logger.info(
            "{}文件未找到，请检查一下输入的文件名称是否正确\n".format(
                list(set(args) - set(file_m))
                )
            )

if __name__ == "__main__":
    dirs = sys.argv[1]
    try:
        names = (sys.argv[2]).split(',')  # 第二个参数为敏感文件名字，非必选
    except IndexError:
        names = []
    with open((dirs + os.path.sep + 'access.log'), 'w') as log:
        os.chmod(dirs + os.path.sep + 'access.log', int('640', 8))
        store = store_all(names)
        find_all(dirs)
        logging.config.dictConfig(logger_json)
        logger = logging.getLogger("root")
        check_x('550')
        check_nx('640')
        check_log('640')
        check_folder('750')
        check_mingan(names, '600')
