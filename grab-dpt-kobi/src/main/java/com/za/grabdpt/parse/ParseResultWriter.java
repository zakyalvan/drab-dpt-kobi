package com.za.grabdpt.parse;

import java.util.List;

public interface ParseResultWriter<T extends ParseResult> {
	void write(T parseResult);
	void write(List<T> parseResults);
}
