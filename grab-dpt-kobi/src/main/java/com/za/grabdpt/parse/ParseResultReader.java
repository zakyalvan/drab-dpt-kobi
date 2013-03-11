package com.za.grabdpt.parse;

import java.util.List;

public interface ParseResultReader<T extends ParseResult> {
	List<T> read();
}
