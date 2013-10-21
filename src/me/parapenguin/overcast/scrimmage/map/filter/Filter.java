package me.parapenguin.overcast.scrimmage.map.filter;

import java.util.List;

import org.dom4j.Element;

import lombok.Getter;

public class Filter {
	
	/*
	 * Having to get used to the OCN XML filter system, this is going to be a challenge...
	 * But then again, I thought that regions would be a challenge, hahah... Then they were easy >;D
	 * 
	 * There are 2 known exception elements - team & block.
	 * <team>[team]</team>
	 * <block>[block name]</block>
	 * 
	 * To get the block name I need to iterate through the materials, get the .name() and replace the "_"s with " "s (underscore with space)
	 * Not all filters will have parents, but I have no clue what to do with a filter without a parent? Maybe it's already defined...
	 * Maybe I'm just going to use a predefined filter, which means I have to apply the same tactics as I did with regions *yay*
	 * This is probably going to be one of the hardest ones to do, right?
	 */
	
	@Getter String name;
	@Getter List<FilterType> parents;
	
	public Filter(Element element) {
		this.name = element.attributeValue("name");
		
		if(this)
	}
	
}
