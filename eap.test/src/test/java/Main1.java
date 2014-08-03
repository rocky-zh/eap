import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;


public class Main1 {
	public static void main(String[] args) {
		String[] arr = {
		"12.1",
		"1.1.2",
		"1.1",
		"1.2",
		"1.1.1",
		"1.2.1.1",
		"2.1"
		};
		
		Arrays.sort(arr, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				String[] arr1 = s1.split("\\.");
				String[] arr2 = s2.split("\\.");
				
				int arr1Len = arr1.length;
				int arr2Len = arr2.length;
				
				for (int i = 0; i < arr1Len && i < arr2Len; i++) {
					if (!arr1[i].equals(arr2[i])) {
						return Integer.parseInt(arr1[i]) < Integer.parseInt(arr2[i]) ? -1 : 1 ;
					}
				}
				
				if (arr1Len != arr2Len) {
					return arr1Len < arr2Len ? -1 : 1;
				}
				
				return 0;
			}
		});
		System.out.println(Arrays.asList(arr));
		
//		List<String> list = new ArrayList();
//		list.add("1.1.2");
//		list.add("1.1");
//		list.add("1.2");
//		list.add("1.1.1");
//		list.add("1.2.1.1");
//		Collections.sort(list);
//		System.out.println(list);
	}
}
