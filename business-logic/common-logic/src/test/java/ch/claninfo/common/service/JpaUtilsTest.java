package ch.claninfo.common.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Id;

import org.junit.Assert;
import org.junit.Test;

import ch.claninfo.clanng.domain.services.JpaUtils;

public class JpaUtilsTest {

	@Test
	public void testLengthA() {
		Assert.assertEquals(300, JpaUtils.getLength(TestEntity.class, "aColumn"));
	}

	@Test
	public void testLengthB() {
		Assert.assertEquals(255, JpaUtils.getLength(TestEntity.class, "bColumn"));
	}

	@Test
	public void testLengthId() {
		Assert.assertEquals(255, JpaUtils.getLength(TestEntity.class, "id"));
	}

	@Test
	public void testDiff() throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		TestEntity t1 = new TestEntity();
		TestEntity t2 = new TestEntity();

		t1.aColumn = "ab";
		t2.aColumn = "ab";

		t1.id = 1;
		t2.id = 3;

		JpaUtils.updateDiff(t1, t2);
		Assert.assertTrue(Objects.equals(t1, t2));
	}

	class TestEntity implements Serializable {

		@Column(length = 300)
		private String aColumn;

		@Column
		private String bColumn;

		@Id
		private Integer id;

		public String getAColumn() {
			return aColumn;
		}

		public void setAColumn(String aColumn) {
			this.aColumn = aColumn;
		}

		public String getBColumn() {
			return bColumn;
		}

		public void setBColumn(String bColumn) {
			this.bColumn = bColumn;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			TestEntity that = (TestEntity) o;
			return Objects.equals(aColumn, that.aColumn) &&
			       Objects.equals(bColumn, that.bColumn) &&
			       Objects.equals(id, that.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(aColumn, bColumn, id);
		}

		@Override
		public String toString() {
			return "TestEntity{" +
			       "aColumn='" + aColumn + '\'' +
			       ", bColumn='" + bColumn + '\'' +
			       ", id=" + id +
			       '}';
		}
	}
}