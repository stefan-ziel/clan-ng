package ch.claninfo.clanng.versicherte.conversion;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.claninfo.clanng.business.logic.conversion.ClassMapper;
import ch.claninfo.clanng.domain.entities.ClanEntity;
import ch.claninfo.clanng.domain.entities.SplittableBo;
import ch.claninfo.clanng.versicherte.entities.Vslohn;
import ch.claninfo.common.dao.BaseBo;
import ch.claninfo.pvclan.bo.VersLohnBo;

public class VersicherteMapper extends ClassMapper {

	private static Map<Class<? extends BaseBo>, List<Class<? extends ClanEntity>>> classMappings;

	static {
		Map<Class<? extends BaseBo>, List<Class<? extends ClanEntity>>> classMappings = new HashMap<>();
		Class<VersLohnBo> boClass = VersLohnBo.class;
		classMappings.put(boClass, Collections.singletonList(Vslohn.class));

		VersicherteMapper.classMappings = Collections.unmodifiableMap(classMappings);
	}

	@Override
	public boolean handles(Class<? extends SplittableBo> bo) {
		return classMappings.containsKey(bo);
	}

	@Override
	public <B extends SplittableBo> Collection<ClanEntity> map(B bo) {
		return defaultMap(bo);
	}

	@Override
	protected List<Class<? extends ClanEntity>> getClassMappings(Class<? extends SplittableBo> bo) {
		return classMappings.get(bo);
	}

	@Override
	protected String toClanEntityProperty(String boProperty) {
		if ("vslgdat".equals(boProperty)) {
			return "gdat";
		} else if ("vslpendenz".equals(boProperty)) {
			return "pendenz";
		}

		return defaultToClanEntityProperty(boProperty);
	}
}