package org.obm.sync.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public class DisplayNameUtils {

	public static String getDisplayName(String commonName, String firstName, String lastName){
		if(!Strings.isNullOrEmpty(commonName)){
			return commonName;
		} else {
			return 
					Joiner.on(' ').skipNulls().join(
						Strings.emptyToNull(firstName),
						Strings.emptyToNull(lastName));
		}
	}
	
}
