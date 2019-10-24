package io.sharpink.persistence.entity.story;

/**
 * Cette Enum représente le genre de l'histoire (fantasy, science fiction,
 * biographie,...)
 *
 */
public enum EnumStoryType {

	FANTASY("FANTASY"), SCI_FI("SCI_FI"), BIOGRAPHY("BIOGRAPHY"), ADVENTURE("ADVENTURE"), ROMANCE("ROMANCE");

	private String value;

	EnumStoryType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
