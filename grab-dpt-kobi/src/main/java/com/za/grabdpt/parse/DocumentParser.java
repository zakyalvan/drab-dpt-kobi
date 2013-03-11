package com.za.grabdpt.parse;

public interface DocumentParser<R extends ParseResult> {
	boolean canParse(String documentContent);
	R parse(String documentContent) throws ParsingException;
}