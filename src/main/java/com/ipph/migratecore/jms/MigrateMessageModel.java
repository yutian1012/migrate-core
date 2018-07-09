package com.ipph.migratecore.jms;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@Accessors(chain=true)
public class MigrateMessageModel implements Serializable {
	private Long tableId;
	private Long batchLogId;
	private Long parentId;
}
