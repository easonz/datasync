package org.zhangpan.datasync.db;

import java.lang.reflect.Field;

import org.zhangpan.utils.EnumItemAnno;

public enum EventType {

	@EnumItemAnno(desc = "修改文件")
	modify,

	@EnumItemAnno(desc = "重命令文件")
	rename,

	@EnumItemAnno(desc = "移动文件")
	move,

	@EnumItemAnno(desc = "删除文件")
	delete,

	@EnumItemAnno(desc = "增加文件")
	add,

	@EnumItemAnno(desc = "任务配置信息")
	config;

	
	public static EventType valueOf(int ordinal) {
		if (ordinal < 0 || ordinal >= values().length) {
			throw new IndexOutOfBoundsException("Invalid ordinal");
		}
		return values()[ordinal];
	}

	public static EventType getByTypeId(String typeId) {
		int ordinal = 0;
		try {
			ordinal = Integer.valueOf(typeId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		return valueOf(ordinal);
	}

	public String getTypeId() {
		return String.valueOf(this.ordinal());
	}

	public String getDesc() {

		Field field = null;
		try {
			field = this.getClass().getDeclaredField(this.name());
		} catch (SecurityException e) {
			e.printStackTrace();
			return "";
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return "";
		}

		EnumItemAnno item = field.getAnnotation(EnumItemAnno.class);
		return item.desc();
	}
}
