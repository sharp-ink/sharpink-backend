package io.sharpink.persistence.entity.story;

/**
 * Cette Enum représente les différents états possibles pour une histoire
 * (terminée, en cours, en stand-by)
 *
 */
public enum StoryStatus {

	COMPLETE("COMPLETE"), // histoire terminée
	PROGRESS("PROGRESS"), // en cours d'écriture
	STAND_BY("STAND_BY"); // au point mort, mise de côté par son auteur

	private String value;

	StoryStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
