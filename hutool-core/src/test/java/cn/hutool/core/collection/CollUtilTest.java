package cn.hutool.core.collection;

import cn.hutool.core.comparator.ComparableComparator;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 集合工具类单元测试
 *
 * @author looly
 */
public class CollUtilTest {

	@Test
	public void testPredicateContains() {
		final ArrayList<String> list = CollUtil.newArrayList("bbbbb", "aaaaa", "ccccc");
		assertTrue(CollUtil.contains(list, s -> s.startsWith("a")));
		assertFalse(CollUtil.contains(list, s -> s.startsWith("d")));
	}

	@Test
	public void testRemoveWithAddIf() {
		ArrayList<Integer> list = CollUtil.newArrayList(1, 2, 3);
		final ArrayList<Integer> exceptRemovedList = CollUtil.newArrayList(2, 3);
		final ArrayList<Integer> exceptResultList = CollUtil.newArrayList(1);

		List<Integer> resultList = CollUtil.removeWithAddIf(list, ele -> 1 == ele);
		assertEquals(list, exceptRemovedList);
		assertEquals(resultList, exceptResultList);

		list = CollUtil.newArrayList(1, 2, 3);
		resultList = new ArrayList<>();
		CollUtil.removeWithAddIf(list, resultList, ele -> 1 == ele);
		assertEquals(list, exceptRemovedList);
		assertEquals(resultList, exceptResultList);
	}

	@Test
	public void testPadLeft() {
		List<String> srcList = CollUtil.newArrayList();
		List<String> answerList = CollUtil.newArrayList("a", "b");
		CollUtil.padLeft(srcList, 1, "b");
		CollUtil.padLeft(srcList, 2, "a");
		assertEquals(srcList, answerList);

		srcList = CollUtil.newArrayList("a", "b");
		answerList = CollUtil.newArrayList("a", "b");
		CollUtil.padLeft(srcList, 2, "a");
		assertEquals(srcList, answerList);

		srcList = CollUtil.newArrayList("c");
		answerList = CollUtil.newArrayList("a", "a", "c");
		CollUtil.padLeft(srcList, 3, "a");
		assertEquals(srcList, answerList);
	}

	@Test
	public void testPadRight() {
		final List<String> srcList = CollUtil.newArrayList("a");
		final List<String> answerList = CollUtil.newArrayList("a", "b", "b", "b", "b");
		CollUtil.padRight(srcList, 5, "b");
		assertEquals(srcList, answerList);
	}

	@SuppressWarnings("ConstantValue")
	@Test
	public void isNotEmptyTest() {
		assertFalse(CollUtil.isNotEmpty((Collection<?>) null));
	}

	@Test
	public void newHashSetTest() {
		final Set<String> set = CollUtil.newHashSet((String[]) null);
		assertNotNull(set);
	}

	@Test
	public void valuesOfKeysTest() {
		final Dict v1 = Dict.create().set("id", 12).set("name", "张三").set("age", 23);
		final Dict v2 = Dict.create().set("age", 13).set("id", 15).set("name", "李四");

		final String[] keys = v1.keySet().toArray(new String[0]);
		final ArrayList<Object> v1s = CollUtil.valuesOfKeys(v1, keys);
		assertTrue(v1s.contains(12));
		assertTrue(v1s.contains(23));
		assertTrue(v1s.contains("张三"));

		final ArrayList<Object> v2s = CollUtil.valuesOfKeys(v2, keys);
		assertTrue(v2s.contains(15));
		assertTrue(v2s.contains(13));
		assertTrue(v2s.contains("李四"));
	}

	@Test
	public void unionTest() {
		final ArrayList<String> list1 = CollUtil.newArrayList("a", "b", "b", "c", "d", "x");
		final ArrayList<String> list2 = CollUtil.newArrayList("a", "b", "b", "b", "c", "d");

		final Collection<String> union = CollUtil.union(list1, list2);

		assertEquals(3, CollUtil.count(union, "b"::equals));
	}

	@Test
	public void intersectionTest() {
		final ArrayList<String> list1 = CollUtil.newArrayList("a", "b", "b", "c", "d", "x");
		final ArrayList<String> list2 = CollUtil.newArrayList("a", "b", "b", "b", "c", "d");

		final Collection<String> intersection = CollUtil.intersection(list1, list2);
		assertEquals(2, CollUtil.count(intersection, "b"::equals));
	}

	@Test
	public void intersectionDistinctTest() {
		final ArrayList<String> list1 = CollUtil.newArrayList("a", "b", "b", "c", "d", "x");
		final ArrayList<String> list2 = CollUtil.newArrayList("a", "b", "b", "b", "c", "d");
		final ArrayList<String> list3 = CollUtil.newArrayList();

		final Collection<String> intersectionDistinct = CollUtil.intersectionDistinct(list1, list2);
		assertEquals(CollUtil.newLinkedHashSet("a", "b", "c", "d"), intersectionDistinct);

		final Collection<String> intersectionDistinct2 = CollUtil.intersectionDistinct(list1, list2, list3);
		assertTrue(intersectionDistinct2.isEmpty());
	}

	@Test
	public void disjunctionTest() {
		final ArrayList<String> list1 = CollUtil.newArrayList("a", "b", "b", "c", "d", "x");
		final ArrayList<String> list2 = CollUtil.newArrayList("a", "b", "b", "b", "c", "d", "x2");

		final Collection<String> disjunction = CollUtil.disjunction(list1, list2);
		assertTrue(disjunction.contains("b"));
		assertTrue(disjunction.contains("x2"));
		assertTrue(disjunction.contains("x"));

		final Collection<String> disjunction2 = CollUtil.disjunction(list2, list1);
		assertTrue(disjunction2.contains("b"));
		assertTrue(disjunction2.contains("x2"));
		assertTrue(disjunction2.contains("x"));
	}

	@Test
	public void disjunctionTest2() {
		// 任意一个集合为空，差集为另一个集合
		final ArrayList<String> list1 = CollUtil.newArrayList();
		final ArrayList<String> list2 = CollUtil.newArrayList("a", "b", "b", "b", "c", "d", "x2");

		final Collection<String> disjunction = CollUtil.disjunction(list1, list2);
		assertEquals(list2, disjunction);
		final Collection<String> disjunction2 = CollUtil.disjunction(list2, list1);
		assertEquals(list2, disjunction2);
	}

	@Test
	public void disjunctionTest3() {
		// 无交集下返回共同的元素
		final ArrayList<String> list1 = CollUtil.newArrayList("1", "2", "3");
		final ArrayList<String> list2 = CollUtil.newArrayList("a", "b", "c");

		final Collection<String> disjunction = CollUtil.disjunction(list1, list2);
		assertTrue(disjunction.contains("1"));
		assertTrue(disjunction.contains("2"));
		assertTrue(disjunction.contains("3"));
		assertTrue(disjunction.contains("a"));
		assertTrue(disjunction.contains("b"));
		assertTrue(disjunction.contains("c"));
		final Collection<String> disjunction2 = CollUtil.disjunction(list2, list1);
		assertTrue(disjunction2.contains("1"));
		assertTrue(disjunction2.contains("2"));
		assertTrue(disjunction2.contains("3"));
		assertTrue(disjunction2.contains("a"));
		assertTrue(disjunction2.contains("b"));
		assertTrue(disjunction2.contains("c"));
	}

	@Test
	public void subtractTest() {
		final List<String> list1 = CollUtil.newArrayList("a", "b", "b", "c", "d", "x");
		final List<String> list2 = CollUtil.newArrayList("a", "b", "b", "b", "c", "d", "x2");
		final Collection<String> subtract = CollUtil.subtract(list1, list2);
		assertEquals(1, subtract.size());
		assertEquals("x", subtract.iterator().next());
	}

	@Test
	public void subtractSetTest() {
		final HashMap<String, Object> map1 = MapUtil.newHashMap();
		final HashMap<String, Object> map2 = MapUtil.newHashMap();
		map1.put("1", "v1");
		map1.put("2", "v2");
		map2.put("2", "v2");
		final Collection<String> r2 = CollUtil.subtract(map1.keySet(), map2.keySet());
		assertEquals("[1]", r2.toString());
	}

	@Test
	public void subtractSetToListTest() {
		final HashMap<String, Object> map1 = MapUtil.newHashMap();
		final HashMap<String, Object> map2 = MapUtil.newHashMap();
		map1.put("1", "v1");
		map1.put("2", "v2");
		map2.put("2", "v2");
		final List<String> r2 = CollUtil.subtractToList(map1.keySet(), map2.keySet());
		assertEquals("[1]", r2.toString());
	}

	@Test
	public void toMapListAndToListMapTest() {
		final HashMap<String, String> map1 = new HashMap<>();
		map1.put("a", "值1");
		map1.put("b", "值1");

		final HashMap<String, String> map2 = new HashMap<>();
		map2.put("a", "值2");
		map2.put("c", "值3");

		// ----------------------------------------------------------------------------------------
		final ArrayList<HashMap<String, String>> list = CollUtil.newArrayList(map1, map2);
		final Map<String, List<String>> map = CollUtil.toListMap(list);
		assertEquals("值1", map.get("a").get(0));
		assertEquals("值2", map.get("a").get(1));

		// ----------------------------------------------------------------------------------------
		final List<Map<String, String>> listMap = CollUtil.toMapList(map);
		assertEquals("值1", listMap.get(0).get("a"));
		assertEquals("值2", listMap.get(1).get("a"));
	}

	@Test
	public void getFieldValuesTest() {
		final Dict v1 = Dict.create().set("id", 12).set("name", "张三").set("age", 23);
		final Dict v2 = Dict.create().set("age", 13).set("id", 15).set("name", "李四");
		final ArrayList<Dict> list = CollUtil.newArrayList(v1, v2);

		final List<Object> fieldValues = CollUtil.getFieldValues(list, "name");
		assertEquals("张三", fieldValues.get(0));
		assertEquals("李四", fieldValues.get(1));
	}

	@Test
	public void splitTest() {
		final ArrayList<Integer> list = CollUtil.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		final List<List<Integer>> split = CollUtil.split(list, 3);
		assertEquals(3, split.size());
		assertEquals(3, split.get(0).size());
	}

	@Test
	public void splitTest2() {
		final ArrayList<Integer> list = CollUtil.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		final List<List<Integer>> split = CollUtil.split(list, Integer.MAX_VALUE);
		assertEquals(1, split.size());
		assertEquals(9, split.get(0).size());
	}

	@Test
	public void foreachTest() {
		final HashMap<String, String> map = MapUtil.newHashMap();
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");

		final String[] result = new String[1];
		final String a = "a";
		CollUtil.forEach(map, (key, value, index) -> {
			if (a.equals(key)) {
				result[0] = value;
			}
		});
		assertEquals("1", result[0]);
	}

	@Test
	public void filterTest() {
		final ArrayList<String> list = CollUtil.newArrayList("a", "b", "c");

		final Collection<String> filtered = CollUtil.edit(list, t -> t + 1);

		assertEquals(CollUtil.newArrayList("a1", "b1", "c1"), filtered);
	}

	@Test
	public void filterTest2() {
		final ArrayList<String> list = CollUtil.newArrayList("a", "b", "c");

		final ArrayList<String> filtered = CollUtil.filter(list, t -> false == "a".equals(t));

		// 原地过滤
		assertSame(list, filtered);
		assertEquals(CollUtil.newArrayList("b", "c"), filtered);
	}

	@Test
	public void filterSetTest() {
		final Set<String> set = CollUtil.newLinkedHashSet("a", "b", "", "  ", "c");
		final Set<String> filtered = CollUtil.filter(set, StrUtil::isNotBlank);

		assertEquals(CollUtil.newLinkedHashSet("a", "b", "c"), filtered);
	}

	@Test
	public void filterRemoveTest() {
		final ArrayList<String> list = CollUtil.newArrayList("a", "b", "c");

		final List<String> removed = new ArrayList<>();
		final ArrayList<String> filtered = CollUtil.filter(list, t -> {
			if ("a".equals(t)) {
				removed.add(t);
				return false;
			}
			return true;
		});

		assertEquals(1, removed.size());
		assertEquals("a", removed.get(0));

		// 原地过滤
		assertSame(list, filtered);
		assertEquals(CollUtil.newArrayList("b", "c"), filtered);
	}

	@Test
	public void removeNullTest() {
		final ArrayList<String> list = CollUtil.newArrayList("a", "b", "c", null, "", "  ");

		final ArrayList<String> filtered = CollUtil.removeNull(list);

		// 原地过滤
		assertSame(list, filtered);
		assertEquals(CollUtil.newArrayList("a", "b", "c", "", "  "), filtered);
	}

	@Test
	public void removeEmptyTest() {
		final ArrayList<String> list = CollUtil.newArrayList("a", "b", "c", null, "", "  ");

		final ArrayList<String> filtered = CollUtil.removeEmpty(list);

		// 原地过滤
		assertSame(list, filtered);
		assertEquals(CollUtil.newArrayList("a", "b", "c", "  "), filtered);
	}

	@Test
	public void removeBlankTest() {
		final ArrayList<String> list = CollUtil.newArrayList("a", "b", "c", null, "", "  ");

		final ArrayList<String> filtered = CollUtil.removeBlank(list);

		// 原地过滤
		assertSame(list, filtered);
		assertEquals(CollUtil.newArrayList("a", "b", "c"), filtered);
	}

	@Test
	public void groupTest() {
		final List<String> list = CollUtil.newArrayList("1", "2", "3", "4", "5", "6");
		final List<List<String>> group = CollUtil.group(list, null);
		assertFalse(group.isEmpty());

		final List<List<String>> group2 = CollUtil.group(list, t -> {
			// 按照奇数偶数分类
			return Integer.parseInt(t) % 2;
		});
		assertEquals(CollUtil.newArrayList("2", "4", "6"), group2.get(0));
		assertEquals(CollUtil.newArrayList("1", "3", "5"), group2.get(1));
	}

	@Test
	public void groupByFieldTest() {
		final List<TestBean> list = CollUtil.newArrayList(new TestBean("张三", 12), new TestBean("李四", 13), new TestBean("王五", 12));
		final List<List<TestBean>> groupByField = CollUtil.groupByField(list, "age");
		assertEquals("张三", groupByField.get(0).get(0).getName());
		assertEquals("王五", groupByField.get(0).get(1).getName());

		assertEquals("李四", groupByField.get(1).get(0).getName());
	}

	@Test
	public void sortByPropertyTest() {
		final List<TestBean> list = CollUtil.newArrayList(
			new TestBean("张三", 12, DateUtil.parse("2018-05-01")), //
			new TestBean("李四", 13, DateUtil.parse("2018-03-01")), //
			new TestBean("王五", 12, DateUtil.parse("2018-04-01"))//
		);

		CollUtil.sortByProperty(list, "createTime");
		assertEquals("李四", list.get(0).getName());
		assertEquals("王五", list.get(1).getName());
		assertEquals("张三", list.get(2).getName());
	}

	@Test
	public void sortByPropertyTest2() {
		final List<TestBean> list = CollUtil.newArrayList(
			new TestBean("张三", 0, DateUtil.parse("2018-05-01")), //
			new TestBean("李四", -12, DateUtil.parse("2018-03-01")), //
			new TestBean("王五", 23, DateUtil.parse("2018-04-01"))//
		);

		CollUtil.sortByProperty(list, "age");
		assertEquals("李四", list.get(0).getName());
		assertEquals("张三", list.get(1).getName());
		assertEquals("王五", list.get(2).getName());
	}

	@Test
	public void fieldValueMapTest() {
		final List<TestBean> list = CollUtil.newArrayList(new TestBean("张三", 12, DateUtil.parse("2018-05-01")), //
			new TestBean("李四", 13, DateUtil.parse("2018-03-01")), //
			new TestBean("王五", 12, DateUtil.parse("2018-04-01"))//
		);

		final Map<String, TestBean> map = CollUtil.fieldValueMap(list, "name");
		assertEquals("李四", map.get("李四").getName());
		assertEquals("王五", map.get("王五").getName());
		assertEquals("张三", map.get("张三").getName());
	}

	@Test
	public void fieldValueAsMapTest() {
		final List<TestBean> list = CollUtil.newArrayList(new TestBean("张三", 12, DateUtil.parse("2018-05-01")), //
			new TestBean("李四", 13, DateUtil.parse("2018-03-01")), //
			new TestBean("王五", 14, DateUtil.parse("2018-04-01"))//
		);

		final Map<String, Integer> map = CollUtil.fieldValueAsMap(list, "name", "age");
		assertEquals(new Integer(12), map.get("张三"));
		assertEquals(new Integer(13), map.get("李四"));
		assertEquals(new Integer(14), map.get("王五"));
	}

	@Test
	public void emptyTest() {
		final SortedSet<String> emptySortedSet = CollUtil.empty(SortedSet.class);
		assertEquals(Collections.emptySortedSet(), emptySortedSet);

		final Set<String> emptySet = CollUtil.empty(Set.class);
		assertEquals(Collections.emptySet(), emptySet);

		final List<String> emptyList = CollUtil.empty(List.class);
		assertEquals(Collections.emptyList(), emptyList);
	}

	@Data
	@AllArgsConstructor
	public static class TestBean {
		private String name;
		private int age;
		private Date createTime;

		public TestBean(final String name, final int age) {
			this.name = name;
			this.age = age;
		}
	}

	@Test
	public void listTest() {
		final List<Object> list1 = CollUtil.list(false);
		final List<Object> list2 = CollUtil.list(true);

		assertInstanceOf(ArrayList.class, list1);
		assertInstanceOf(LinkedList.class, list2);
	}

	@Test
	public void listTest2() {
		final List<String> list1 = CollUtil.list(false, "a", "b", "c");
		final List<String> list2 = CollUtil.list(true, "a", "b", "c");
		assertEquals("[a, b, c]", list1.toString());
		assertEquals("[a, b, c]", list2.toString());
	}

	@Test
	public void listTest3() {
		final HashSet<String> set = new LinkedHashSet<>();
		set.add("a");
		set.add("b");
		set.add("c");

		final List<String> list1 = CollUtil.list(false, set);
		final List<String> list2 = CollUtil.list(true, set);
		assertEquals("[a, b, c]", list1.toString());
		assertEquals("[a, b, c]", list2.toString());
	}

	@Test
	public void getTest() {
		final HashSet<String> set = CollUtil.set(true, "A", "B", "C", "D");
		String str = CollUtil.get(set, 2);
		assertEquals("C", str);

		str = CollUtil.get(set, -1);
		assertEquals("D", str);
	}

	@Test
	public void addAllIfNotContainsTest() {
		final ArrayList<String> list1 = new ArrayList<>();
		list1.add("1");
		list1.add("2");
		final ArrayList<String> list2 = new ArrayList<>();
		list2.add("2");
		list2.add("3");
		CollUtil.addAllIfNotContains(list1, list2);

		assertEquals(3, list1.size());
		assertEquals("1", list1.get(0));
		assertEquals("2", list1.get(1));
		assertEquals("3", list1.get(2));
	}

	@Test
	public void subInput1PositiveNegativePositiveOutput1() {
		// Arrange
		final List<Integer> list = new ArrayList<>();
		list.add(null);
		final int start = 3;
		final int end = -1;
		final int step = 2;
		// Act
		final List<Integer> retval = CollUtil.sub(list, start, end, step);
		// Assert result
		final List<Integer> arrayList = new ArrayList<>();
		arrayList.add(null);
		assertEquals(arrayList, retval);
	}

	@Test
	public void subInput1ZeroPositivePositiveOutput1() {
		// Arrange
		final List<Integer> list = new ArrayList<>();
		list.add(null);
		final int start = 0;
		final int end = 1;
		final int step = 2;
		// Act
		final List<Integer> retval = CollUtil.sub(list, start, end, step);

		// Assert result
		final List<Integer> arrayList = new ArrayList<>();
		arrayList.add(null);
		assertEquals(arrayList, retval);
	}

	@Test
	public void subInput1PositiveZeroOutput0() {
		// Arrange
		final List<Integer> list = new ArrayList<>();
		list.add(null);
		final int start = 1;
		final int end = 0;
		// Act
		final List<Integer> retval = CollUtil.sub(list, start, end);

		// Assert result
		final List<Integer> arrayList = new ArrayList<>();
		assertEquals(arrayList, retval);
	}

	@Test
	public void subInput0ZeroZeroZeroOutputNull() {
		// Arrange
		final List<Integer> list = new ArrayList<>();
		final int start = 0;
		final int end = 0;
		final int step = 0;
		// Act
		final List<Integer> retval = CollUtil.sub(list, start, end, step);
		// Assert result
		assertTrue(retval.isEmpty());
	}

	@Test
	public void subInput1PositiveNegativeZeroOutput0() {
		// Arrange
		final List<Integer> list = new ArrayList<>();
		list.add(null);
		final int start = 1;
		final int end = -2_147_483_648;
		final int step = 0;
		// Act
		final List<Integer> retval = CollUtil.sub(list, start, end, step);
		// Assert result
		final List<Integer> arrayList = new ArrayList<>();
		assertEquals(arrayList, retval);
	}

	@Test
	public void subInput1PositivePositivePositiveOutput0() {
		// Arrange
		final List<Integer> list = new ArrayList<>();
		list.add(null);
		final int start = 2_147_483_647;
		final int end = 2_147_483_647;
		final int step = 1_073_741_824;
		// Act
		final List<Integer> retval = CollUtil.sub(list, start, end, step);
		// Assert result
		final List<Integer> arrayList = new ArrayList<>();
		assertEquals(arrayList, retval);
	}

	@Test
	public void subInput1PositiveNegativePositiveOutputArrayIndexOutOfBoundsException() {
		assertThrows(IndexOutOfBoundsException.class, () -> {
			// Arrange
			final List<Integer> list = new ArrayList<>();
			list.add(null);
			final int start = 2_147_483_643;
			final int end = -2_147_483_648;
			final int step = 2;

			// Act
			CollUtil.sub(list, start, end, step);
			// Method is not expected to return due to exception thrown
		});
	}

	@Test
	public void subInput0ZeroPositiveNegativeOutputNull() {
		// Arrange
		final List<Integer> list = new ArrayList<>();
		final int start = 0;
		final int end = 1;
		final int step = -2_147_483_646;
		// Act
		final List<Integer> retval = CollUtil.sub(list, start, end, step);
		// Assert result
		assertTrue(retval.isEmpty());
	}

	@Test
	public void subInput1PositivePositivePositiveOutput02() {
		// Arrange
		final List<Integer> list = new ArrayList<>();
		list.add(null);
		final int start = 2_147_483_643;
		final int end = 2_147_483_642;
		final int step = 1_073_741_824;
		// Act
		final List<Integer> retval = CollUtil.sub(list, start, end, step);
		// Assert result
		final List<Integer> arrayList = new ArrayList<>();
		assertEquals(arrayList, retval);
	}

	@Test
	public void subInput1ZeroZeroPositiveOutput0() {
		// Arrange
		final List<Integer> list = new ArrayList<>();
		list.add(0);
		final int start = 0;
		final int end = 0;
		final int step = 2;
		// Act
		final List<Integer> retval = CollUtil.sub(list, start, end, step);
		// Assert result
		final List<Integer> arrayList = new ArrayList<>();
		assertEquals(arrayList, retval);
	}

	@Test
	public void subInput1NegativeZeroPositiveOutput0() {
		// Arrange
		final List<Integer> list = new ArrayList<>();
		list.add(0);
		final int start = -1;
		final int end = 0;
		final int step = 2;
		// Act
		final List<Integer> retval = CollUtil.sub(list, start, end, step);
		// Assert result
		final List<Integer> arrayList = new ArrayList<>();
		assertEquals(arrayList, retval);
	}

	@Test
	public void subInput0ZeroZeroOutputNull() {
		// Arrange
		final List<Integer> list = new ArrayList<>();
		final int start = 0;
		final int end = 0;
		// Act
		final List<Integer> retval = CollUtil.sub(list, start, end);
		// Assert result
		assertTrue(retval.isEmpty());
	}

	@Test
	public void sortPageAllTest() {
		final List<Integer> list = CollUtil.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		final List<Integer> sortPageAll = CollUtil.sortPageAll(1, 5, Comparator.reverseOrder(), list);

		assertEquals(CollUtil.newArrayList(4, 3, 2, 1), sortPageAll);
	}

	@Test
	public void containsAnyTest() {
		final ArrayList<Integer> list1 = CollUtil.newArrayList(1, 2, 3, 4, 5);
		final ArrayList<Integer> list2 = CollUtil.newArrayList(5, 3, 1, 9, 11);

		assertTrue(CollUtil.containsAny(list1, list2));
	}

	@Test
	public void containsAllTest() {
		final ArrayList<Integer> list1 = CollUtil.newArrayList(1, 2, 3, 4, 5);
		final ArrayList<Integer> list2 = CollUtil.newArrayList(5, 3, 1);
		assertTrue(CollUtil.containsAll(list1, list2));

		final ArrayList<Integer> list3 = CollUtil.newArrayList(1);
		final ArrayList<Integer> list4 = CollUtil.newArrayList();
		assertTrue(CollUtil.containsAll(list3, list4));
	}

	@Test
	public void getLastTest() {
		// 测试：空数组返回null而不是报错
		final List<String> test = CollUtil.newArrayList();
		final String last = CollUtil.getLast(test);
		assertNull(last);
	}

	@Test
	public void zipTest() {
		final Collection<String> keys = CollUtil.newArrayList("a", "b", "c", "d");
		final Collection<Integer> values = CollUtil.newArrayList(1, 2, 3, 4);

		final Map<String, Integer> map = CollUtil.zip(keys, values);

		assertEquals(4, Objects.requireNonNull(map).size());

		assertEquals(1, map.get("a").intValue());
		assertEquals(2, map.get("b").intValue());
		assertEquals(3, map.get("c").intValue());
		assertEquals(4, map.get("d").intValue());
	}

	@Test
	public void toMapTest() {
		final Collection<String> keys = CollUtil.newArrayList("a", "b", "c", "d");
		final Map<String, String> map = CollUtil.toMap(keys, new HashMap<>(), (value) -> "key" + value);
		assertEquals("a", map.get("keya"));
		assertEquals("b", map.get("keyb"));
		assertEquals("c", map.get("keyc"));
		assertEquals("d", map.get("keyd"));
	}

	@Test
	public void addIfAbsentTest() {
		// 为false的情况
		assertFalse(CollUtil.addIfAbsent(null, null));
		assertFalse(CollUtil.addIfAbsent(CollUtil.newArrayList(), null));
		assertFalse(CollUtil.addIfAbsent(null, "123"));
		assertFalse(CollUtil.addIfAbsent(CollUtil.newArrayList("123"), "123"));
		assertFalse(CollUtil.addIfAbsent(CollUtil.newArrayList(new Animal("jack", 20)),
			new Animal("jack", 20)));

		// 正常情况
		assertTrue(CollUtil.addIfAbsent(CollUtil.newArrayList("456"), "123"));
		assertTrue(CollUtil.addIfAbsent(CollUtil.newArrayList(new Animal("jack", 20)),
			new Dog("jack", 20)));
		assertTrue(CollUtil.addIfAbsent(CollUtil.newArrayList(new Animal("jack", 20)),
			new Animal("tom", 20)));
	}

	@Test
	public void mapToMapTest() {
		final HashMap<String, String> oldMap = new HashMap<>();
		oldMap.put("a", "1");
		oldMap.put("b", "12");
		oldMap.put("c", "134");

		final Map<String, Long> map = CollUtil.toMap(oldMap.entrySet(),
			new HashMap<>(),
			Map.Entry::getKey,
			entry -> Long.parseLong(entry.getValue()));

		assertEquals(1L, (long) map.get("a"));
		assertEquals(12L, (long) map.get("b"));
		assertEquals(134L, (long) map.get("c"));
	}

	@Test
	public void countMapTest() {
		final ArrayList<String> list = CollUtil.newArrayList("a", "b", "c", "c", "a", "b", "d");
		final Map<String, Integer> countMap = CollUtil.countMap(list);

		assertEquals(Integer.valueOf(2), countMap.get("a"));
		assertEquals(Integer.valueOf(2), countMap.get("b"));
		assertEquals(Integer.valueOf(2), countMap.get("c"));
		assertEquals(Integer.valueOf(1), countMap.get("d"));
	}

	@Test
	public void indexOfTest() {
		final ArrayList<String> list = CollUtil.newArrayList("a", "b", "c", "c", "a", "b", "d");
		final int i = CollUtil.indexOf(list, (str) -> str.charAt(0) == 'c');
		assertEquals(2, i);
	}

	@Test
	public void lastIndexOfTest() {
		// List有优化
		final ArrayList<String> list = CollUtil.newArrayList("a", "b", "c", "c", "a", "b", "d");
		final int i = CollUtil.lastIndexOf(list, (str) -> str.charAt(0) == 'c');
		assertEquals(3, i);
	}

	@Test
	public void lastIndexOfSetTest() {
		final Set<String> list = CollUtil.set(true, "a", "b", "c", "c", "a", "b", "d");
		// 去重后c排第三
		final int i = CollUtil.lastIndexOf(list, (str) -> str.charAt(0) == 'c');
		assertEquals(2, i);
	}

	@Test
	public void pageTest() {
		final List<Dict> objects = CollUtil.newArrayList();
		for (int i = 0; i < 10; i++) {
			objects.add(Dict.create().set("name", "姓名：" + i));
		}

		assertEquals(0, CollUtil.page(3, 5, objects).size());
	}

	@Test
	public void subtractToListTest() {
		final List<Long> list1 = Arrays.asList(1L, 2L, 3L);
		final List<Long> list2 = Arrays.asList(2L, 3L);

		final List<Long> result = CollUtil.subtractToList(list1, list2);
		assertEquals(1, result.size());
		assertEquals(1L, (long) result.get(0));
	}

	@Test
	public void sortComparableTest() {
		final List<String> of = ListUtil.toList("a", "c", "b");
		final List<String> sort = CollUtil.sort(of, new ComparableComparator<>());
		assertEquals("a,b,c", CollUtil.join(sort, ","));
	}

	@Test
	public void setValueByMapTest() {
		// https://gitee.com/dromara/hutool/pulls/482
		final List<Person> people = Arrays.asList(
			new Person("aa", 12, "man", 1),
			new Person("bb", 13, "woman", 2),
			new Person("cc", 14, "man", 3),
			new Person("dd", 15, "woman", 4),
			new Person("ee", 16, "woman", 5),
			new Person("ff", 17, "man", 6)
		);

		final Map<Integer, String> genderMap = new HashMap<>();
		genderMap.put(1, null);
		genderMap.put(2, "妇女");
		genderMap.put(3, "少女");
		genderMap.put(4, "女");
		genderMap.put(5, "小孩");
		genderMap.put(6, "男");

		assertEquals(people.get(1).getGender(), "woman");
		CollUtil.setValueByMap(people, genderMap, Person::getId, Person::setGender);
		assertEquals(people.get(1).getGender(), "妇女");

		final Map<Integer, Person> personMap = new HashMap<>();
		personMap.put(1, new Person("AA", 21, "男", 1));
		personMap.put(2, new Person("BB", 7, "小孩", 2));
		personMap.put(3, new Person("CC", 65, "老人", 3));
		personMap.put(4, new Person("DD", 35, "女人", 4));
		personMap.put(5, new Person("EE", 14, "少女", 5));
		personMap.put(6, null);

		CollUtil.setValueByMap(people, personMap, Person::getId, (x, y) -> {
			x.setGender(y.getGender());
			x.setName(y.getName());
			x.setAge(y.getAge());
		});

		assertEquals(people.get(1).getGender(), "小孩");
	}

	@Test
	public void distinctTest() {
		final ArrayList<Integer> distinct = CollUtil.distinct(ListUtil.of(5, 3, 10, 9, 0, 5, 10, 9));
		assertEquals(ListUtil.of(5, 3, 10, 9, 0), distinct);
	}

	@Test
	public void distinctByFunctionTest() {
		final List<Person> people = Arrays.asList(
			new Person("aa", 12, "man", 1),
			new Person("bb", 13, "woman", 2),
			new Person("cc", 14, "man", 3),
			new Person("dd", 15, "woman", 4),
			new Person("ee", 16, "woman", 5),
			new Person("ff", 17, "man", 6)
		);

		// 覆盖模式下ff覆盖了aa，ee覆盖了bb
		List<Person> distinct = CollUtil.distinct(people, Person::getGender, true);
		assertEquals(2, distinct.size());
		assertEquals("ff", distinct.get(0).getName());
		assertEquals("ee", distinct.get(1).getName());

		// 非覆盖模式下，保留了最早加入的aa和bb
		distinct = CollUtil.distinct(people, Person::getGender, false);
		assertEquals(2, distinct.size());
		assertEquals("aa", distinct.get(0).getName());
		assertEquals("bb", distinct.get(1).getName());
	}

	@SuppressWarnings("ConstantValue")
	@Test
	public void unionNullTest() {
		final List<String> list1 = new ArrayList<>();
		final List<String> list2 = null;
		final List<String> list3 = null;
		final Collection<String> union = CollUtil.union(list1, list2, list3);
		assertNotNull(union);
	}

	@SuppressWarnings("ConstantValue")
	@Test
	public void unionDistinctNullTest() {
		final List<String> list1 = new ArrayList<>();
		final List<String> list2 = null;
		final List<String> list3 = null;
		final Set<String> set = CollUtil.unionDistinct(list1, list2, list3);
		assertNotNull(set);
	}

	@SuppressWarnings({"ConfusingArgumentToVarargsMethod", "ConstantValue"})
	@Test
	public void unionAllNullTest() {
		final List<String> list1 = new ArrayList<>();
		final List<String> list2 = null;
		final List<String> list3 = null;
		final List<String> list = CollUtil.unionAll(list1, list2, list3);
		assertNotNull(list);

		final List<String> resList2 = CollUtil.unionAll(null, null, null);
		assertNotNull(resList2);
	}

	@Test
	public void unionAllOrdinaryTest() {
		final List<Integer> list1 = CollectionUtil.newArrayList(1, 2, 2, 3, 3);
		final List<Integer> list2 = CollectionUtil.newArrayList(1, 2, 3);
		final List<Integer> list3 = CollectionUtil.newArrayList(4, 5, 6);
		final List<Integer> list = CollUtil.unionAll(list1, list2, list3);
		assertNotNull(list);
		assertArrayEquals(
			CollectionUtil.newArrayList(1, 2, 2, 3, 3, 1, 2, 3, 4, 5, 6).toArray(),
			list.toArray());
	}

	@Test
	public void unionAllTwoOrdinaryTest() {
		final List<Integer> list1 = CollectionUtil.newArrayList(1, 2, 2, 3, 3);
		final List<Integer> list2 = CollectionUtil.newArrayList(1, 2, 3);
		final List<Integer> list = CollUtil.unionAll(list1, list2);
		assertNotNull(list);
		assertArrayEquals(
			CollectionUtil.newArrayList(1, 2, 2, 3, 3, 1, 2, 3).toArray(),
			list.toArray());
	}

	@Test
	public void unionAllOtherIsNullTest() {
		final List<Integer> list1 = CollectionUtil.newArrayList(1, 2, 2, 3, 3);
		final List<Integer> list2 = CollectionUtil.newArrayList(1, 2, 3);
		@SuppressWarnings("ConfusingArgumentToVarargsMethod") final List<Integer> list = CollUtil.unionAll(list1, list2, null);
		assertNotNull(list);
		assertArrayEquals(
			CollectionUtil.newArrayList(1, 2, 2, 3, 3, 1, 2, 3).toArray(),
			list.toArray());
	}

	@Test
	public void unionAllOtherTwoNullTest() {
		final List<Integer> list1 = CollectionUtil.newArrayList(1, 2, 2, 3, 3);
		final List<Integer> list2 = CollectionUtil.newArrayList(1, 2, 3);
		final List<Integer> list = CollUtil.unionAll(list1, list2, null, null);
		assertNotNull(list);
		assertArrayEquals(
			CollectionUtil.newArrayList(1, 2, 2, 3, 3, 1, 2, 3).toArray(),
			list.toArray());
	}

	@SuppressWarnings("ConstantValue")
	@Test
	public void intersectionNullTest() {
		final List<String> list1 = new ArrayList<>();
		list1.add("aa");
		final List<String> list2 = new ArrayList<>();
		list2.add("aa");
		final List<String> list3 = null;
		final Collection<String> collection = CollUtil.intersection(list1, list2, list3);
		assertNotNull(collection);
	}

	@Test
	public void intersectionDistinctNullTest() {
		final List<String> list1 = new ArrayList<>();
		list1.add("aa");
		final List<String> list2 = null;
		// list2.add("aa");
		final List<String> list3 = null;
		final Collection<String> collection = CollUtil.intersectionDistinct(list1, list2, list3);
		assertNotNull(collection);
	}

	@Data
	@AllArgsConstructor
	static class Person {
		private String name;
		private Integer age;
		private String gender;
		private Integer id;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Animal {
		private String name;
		private Integer age;
	}

	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	@Data
	static class Dog extends Animal {

		public Dog(String name, Integer age) {
			super(name, age);
		}
	}

	@Test
	public void getFirstTest() {
		final List<?> nullList = null;
		final Object first = CollUtil.getFirst(nullList);
		assertNull(first);
	}

	@Test
	public void testMatch() {
		List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
		assertTrue(CollUtil.anyMatch(list, i -> i == 1));
		assertFalse(CollUtil.anyMatch(list, i -> i > 6));
		assertFalse(CollUtil.allMatch(list, i -> i == 1));
		assertTrue(CollUtil.allMatch(list, i -> i <= 6));
	}

	@Test
	public void maxTest() {
		List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
		assertEquals((Integer) 6, CollUtil.max(list));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void maxEmptyTest() {
		final List<? extends Comparable> emptyList = Collections.emptyList();
		assertNull(CollUtil.max(emptyList));
	}

	@Test
	public void minNullTest() {
		assertNull(CollUtil.max(null));
	}

	@Test
	public void issueI8Z2Q4Test() {
		ArrayList<String> coll1 = new ArrayList<>();
		coll1.add("1");
		coll1.add("2");
		coll1.add("3");
		coll1.add("4");
		ArrayList<String> coll2 = new ArrayList<>();
		coll2.add("1");
		coll2.add("1");
		coll2.add("1");
		coll2.add("1");
		coll2.add("1");

		assertTrue(CollUtil.containsAll(coll1, coll2));
	}
}
