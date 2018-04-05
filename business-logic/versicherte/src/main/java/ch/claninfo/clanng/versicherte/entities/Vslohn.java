package ch.claninfo.clanng.versicherte.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import ch.claninfo.clanng.domain.constraints.LohnArtConstraint;
import ch.claninfo.clanng.domain.entities.HistEntity;
import ch.claninfo.clanng.domain.types.SexCd;
import ch.claninfo.clanng.versicherte.entities.pks.VslohnPk;

@Entity
@Table(schema = "PEKA")
@IdClass(VslohnPk.class)
public class Vslohn extends HistEntity {

	@NotNull
	@Digits(integer = 8, fraction = 0)
	@Id
	private BigDecimal vsnum;

	@NotNull
	@Id
	private LocalDate gdat;

	@LohnArtConstraint
	private Long lohnart;

	@NotNull
	@Digits(integer = 14, fraction = 2)
	private BigDecimal vslohn;

	@Digits(integer = 14, fraction = 2)
	private BigDecimal vspraem;

	@NotNull
	@Digits(integer = 8, fraction = 4)
	private BigDecimal vsbegrd;

	@NotNull
	@Digits(integer = 8, fraction = 4)
	private BigDecimal vspbgrd;

	@NotNull
	@Digits(integer = 8, fraction = 4)
	private BigDecimal ivgrad;

	@NotNull
	@Digits(integer = 1, fraction = 0)
	private Byte pendenz;

	private LocalDateTime gebdat;
	private SexCd sexcd;

	@Digits(integer = 1, fraction = 0)
	private Byte zivcd;

	@Digits(integer = 1, fraction = 0)
	private Byte upfcd;

	@Digits(integer = 2, fraction = 0)
	private Byte fil1cd;

	@Digits(integer = 2, fraction = 0)
	private Byte fil2cd;

	public Byte getFil1cd() {
		return fil1cd;
	}

	public Byte getFil2cd() {
		return fil2cd;
	}

	public LocalDate getGdat() {
		return gdat;
	}

	public LocalDateTime getGebdat() {
		return gebdat;
	}

	public BigDecimal getIvgrad() {
		return ivgrad;
	}

	public Long getLohnart() {
		return lohnart;
	}

	public Byte getPendenz() {
		return pendenz;
	}

	public SexCd getSexcd() {
		return sexcd;
	}

	public Byte getUpfcd() {
		return upfcd;
	}

	public BigDecimal getVsbegrd() {
		return vsbegrd;
	}

	public BigDecimal getVslohn() {
		return vslohn;
	}

	public BigDecimal getVsnum() {
		return vsnum;
	}

	public BigDecimal getVspbgrd() {
		return vspbgrd;
	}

	public BigDecimal getVspraem() {
		return vspraem;
	}

	public Byte getZivcd() {
		return zivcd;
	}

	public void setFil1cd(Byte fil1Cd) {
		this.fil1cd = fil1Cd;
	}

	public void setFil2cd(Byte fil2Cd) {
		this.fil2cd = fil2Cd;
	}

	public void setGdat(LocalDate gdat) {
		this.gdat = gdat;
	}

	public void setGebdat(LocalDateTime gebdat) {
		this.gebdat = gebdat;
	}

	public void setIvgrad(BigDecimal ivgrad) {
		this.ivgrad = ivgrad;
	}

	public void setLohnart(Long lohnart) {
		this.lohnart = lohnart;
	}

	public void setPendenz(Byte pendenz) {
		this.pendenz = pendenz;
	}

	public void setSexcd(SexCd sexcd) {
		this.sexcd = sexcd;
	}

	public void setUpfcd(Byte upfcd) {
		this.upfcd = upfcd;
	}

	public void setVsbegrd(BigDecimal vsbegrd) {
		this.vsbegrd = vsbegrd;
	}

	public void setVslohn(BigDecimal vslohn) {
		this.vslohn = vslohn;
	}

	public void setVsnum(BigDecimal vsnum) {
		this.vsnum = vsnum;
	}

	public void setVspbgrd(BigDecimal vspbgrd) {
		this.vspbgrd = vspbgrd;
	}

	public void setVspraem(BigDecimal vspraem) {
		this.vspraem = vspraem;
	}

	public void setZivcd(Byte zivcd) {
		this.zivcd = zivcd;
	}

	@Override
	public String toString() {
		return "Vslohn{" + "vsnum=" + vsnum + ", gdat=" + gdat + ", lohnart=" + lohnart + ", vslohn=" + vslohn + ", vspraem=" + vspraem + ", vsbegrd=" + vsbegrd + ", vspbgrd=" + vspbgrd + ", ivgrad=" + ivgrad + ", pendenz=" + pendenz + ", gebdat=" + gebdat + ", sexcd=" + sexcd + ", zivcd=" + zivcd + ", upfcd=" + upfcd + ", fil1cd=" + fil1cd + ", fil2cd=" + fil2cd + '}';
	}
}
