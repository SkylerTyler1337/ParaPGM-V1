package me.parapenguin.overcast.scrimmage.map.region;

import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.map.filter.Filter;

public class FilteredRegion {
	
	@Getter List<RegionGroup> regions;
	@Getter List<Filter> filters;
	
	public FilteredRegion(List<RegionGroup> regions, List<Filter> filters) {
		this.regions = regions;
		this.filters = filters;
	}
	
}
