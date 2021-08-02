package com.hqy.common.enums;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rosun:285350134@qq.com
 *
 */
public enum CountryEnum {
	AD("AD", "安道尔共和国", "Andorra", 376), AE("AE", "阿拉伯联合酋长国", "United Arab Emirates", 971),
	AF("AF", "阿富汗", "Afghanistan", 93), AG("AG", "安提瓜和巴布达", "Antigua and Barbuda", 1268),
	AI("AI", "安圭拉岛", "Anguilla", 1264), AL("AL", "阿尔巴尼亚", "Albania", 355), AM("AM", "亚美尼亚", "Armenia", 374),
	AO("AO", "安哥拉", "Angola", 244), AR("AR", "阿根廷", "Argentina", 54), AT("AT", "奥地利", "Austria", 43),
	AU("AU", "澳大利亚", "Australia", 61), AZ("AZ", "阿塞拜疆", "Azerbaijan", 994), BB("BB", "巴巴多斯", "Barbados", 1246),
	BD("BD", "孟加拉国", "Bangladesh", 880), BE("BE", "比利时", "Belgium", 32), BF("BF", "布基纳法索", "Burkina Faso", 226),
	BG("BG", "保加利亚", "Bulgaria", 359), BH("BH", "巴林", "Bahrain", 973), BI("BI", "布隆迪", "Burundi", 257),
	BJ("BJ", "贝宁", "Benin", 229), BL("BL", "巴勒斯坦", "Palestine", 970), BM("BM", "百慕大群岛", "Bermuda", 1441),
	BN("BN", "文莱", "Brunei Darussalam", 673), BO("BO", "玻利维亚", "Bolivia", 591), BR("BR", "巴西", "Brazil", 55),
	BS("BS", "巴哈马", "Bahamas", 1242), BW("BW", "博茨瓦纳", "Botswana", 267), BY("BY", "白俄罗斯", "Belarus", 375),
	BZ("BZ", "伯利兹", "Belize", 501), CA("CA", "加拿大", "Canada", 1), CF("CF", "中非共和国", "Central African Republic", 236),
	CG("CG", "刚果", "Congo", 242), CH("CH", "瑞士", "Switzerland", 41), CK("CK", "库克群岛", "Cook Islands", 682),
	CL("CL", "智利", "Chile", 56), CM("CM", "喀麦隆", "Cameroon", 237), CN("CN", "中国", "China", 86),
	CO("CO", "哥伦比亚", "Colombia", 57), CR("CR", "哥斯达黎加", "Costa Rica", 506), CS("CS", "捷克", "Czech", 420),
	CU("CU", "古巴", "Cuba", 53), CY("CY", "塞浦路斯", "Cyprus", 357), CZ("CZ", "捷克", "Czech Republic", 420),
	DE("DE", "德国", "Germany", 49), DJ("DJ", "吉布提", "Djibouti", 253), DK("DK", "丹麦", "Denmark", 45),
	DO("DO", "多米尼加共和国", "Dominican Republic", 1890), DZ("DZ", "阿尔及利亚", "Algeria", 213), EC("EC", "厄瓜多尔", "Ecuador", 593),
	EE("EE", "爱沙尼亚", "Estonia", 372), EG("EG", "埃及", "Egypt", 20), ES("ES", "西班牙", "Spain", 34),
	ET("ET", "埃塞俄比亚", "Ethiopia", 251), FI("FI", "芬兰", "Finland", 358), FJ("FJ", "斐济", "Fiji", 679),
	FR("FR", "法国", "France", 33), GA("GA", "加蓬", "Gabon", 241), GB("GB", "英国", "United Kingdom", 44),
	GD("GD", "格林纳达", "Grenada", 1809), GE("GE", "格鲁吉亚", "Georgia", 995), GF("GF", "法属圭亚那", "French Guiana", 594),
	GH("GH", "加纳", "Ghana", 233), GI("GI", "直布罗陀", "Gibraltar", 350), GM("GM", "冈比亚", "Gambia", 220),
	GN("GN", "几内亚", "Guinea", 224), GR("GR", "希腊", "Greece", 30), GT("GT", "危地马拉", "Guatemala", 502),
	GU("GU", "关岛", "Guam", 1671), GY("GY", "圭亚那", "Guyana", 592), HK("HK", "中国香港", "Hong Kong", 852),
	HN("HN", "洪都拉斯", "Honduras", 504), HT("HT", "海地", "Haiti", 509), HU("HU", "匈牙利", "Hungary", 36),
	ID("ID", "印度尼西亚", "Indonesia", 62), IE("IE", "爱尔兰", "Ireland", 353), IL("IL", "以色列", "Israel", 972),
	IN("IN", "印度", "India", 91), IQ("IQ", "伊拉克", "Iraq", 964), IR("IR", "伊朗", "Iran", 98),
	IS("IS", "冰岛", "Iceland", 354), IT("IT", "意大利", "Italy", 39), JM("JM", "牙买加", "Jamaica", 1876),
	JO("JO", "约旦", "Jordan", 962), JP("JP", "日本", "Japan", 81), KE("KE", "肯尼亚", "Kenya", 254),
	KG("KG", "吉尔吉斯坦", "Kyrgyzstan", 331), KH("KH", "柬埔寨", "Cambodia", 855),
	KP("KP", "朝鲜", "Korea Democratic People's Republic of", 850), KR("KR", "韩国", "Korea Republic of", 82),
	KT("KT", "科特迪瓦共和国", "Republic of Ivory Coast", 225), KW("KW", "科威特", "Kuwait", 965),
	KZ("KZ", "哈萨克斯坦", "Kazakhstan", 327), LA("LA", "老挝", "Lao People's Democratic Republic", 856), LB("LB", "黎巴嫩", "Lebanon", 961),
	LC("LC", "圣卢西亚", "Saint Lucia", 1758), LI("LI", "列支敦士登", "Liechtenstein", 423), LK("LK", "斯里兰卡", "Sri Lanka", 94),
	LR("LR", "利比里亚", "Liberia", 231), LS("LS", "莱索托", "Lesotho", 266), LT("LT", "立陶宛", "Lithuania", 370),
	LU("LU", "卢森堡", "Luxembourg", 352), LV("LV", "拉脱维亚", "Latvia", 371), LY("LY", "利比亚", "Libya", 218),
	MA("MA", "摩洛哥", "Morocco", 212), MC("MC", "摩纳哥", "Monaco", 377), MD("MD", "摩尔多瓦", "Moldova", 373),
	MG("MG", "马达加斯加", "Madagascar", 261), ML("ML", "马里", "Mali", 223), MM("MM", "缅甸", "Myanmar", 95),
	MN("MN", "蒙古", "Mongolia", 976), MO("MO", "中国澳门", "Macao", 853), MS("MS", "蒙特塞拉特岛", "Montserrat", 1664),
	MT("MT", "马耳他", "Malta", 356), MU("MU", "毛里求斯", "Mauritius", 230), MV("MV", "马尔代夫", "Maldives", 960),
	MW("MW", "马拉维", "Malawi", 265), MX("MX", "墨西哥", "Mexico", 52), MY("MY", "马来西亚", "Malaysia", 60),
	MZ("MZ", "莫桑比克", "Mozambique", 258), NA("NA", "纳米比亚", "Namibia", 264), NE("NE", "尼日尔", "Niger", 977),
	NG("NG", "尼日利亚", "Nigeria", 234), NI("NI", "尼加拉瓜", "Nicaragua", 505), NL("NL", "荷兰", "Netherlands", 31),
	NO("NO", "挪威", "Norway", 47), NP("NP", "尼泊尔", "Nepal", 977), NR("NR", "瑙鲁", "Nauru", 674),
	NZ("NZ", "新西兰", "New Zealand", 64), OM("OM", "阿曼", "Oman", 968), PA("PA", "巴拿马", "Panama", 507),
	PE("PE", "秘鲁", "Peru", 51), PF("PF", "法属玻利尼西亚", "French Polynesia", 689),
	PG("PG", "巴布亚新几内亚", "Papua New Guinea", 675), PH("PH", "菲律宾", "Philippines", 63), PK("PK", "巴基斯坦", "Pakistan", 92),
	PL("PL", "波兰", "Poland", 48), PR("PR", "波多黎各", "Puerto Rico", 1787), PT("PT", "葡萄牙", "Portugal", 351),
	PY("PY", "巴拉圭", "Paraguay", 595), QA("QA", "卡塔尔", "Qatar", 974), RO("RO", "罗马尼亚", "Romania", 40),
	RU("RU", "俄罗斯", "Russian Federation", 7), SA("SA", "沙特阿拉伯", "Saudi Arabia", 966), SB("SB", "所罗门群岛", "Solomon Islands", 677),
	SC("SC", "塞舌尔", "Seychelles", 248), SD("SD", "苏丹", "Sudan", 249), SE("SE", "瑞典", "Sweden", 46),
	SG("SG", "新加坡", "Singapore", 65), SI("SI", "斯洛文尼亚", "Slovenia", 386), SK("SK", "斯洛伐克", "Slovakia (SLOVAK Republic)", 421),
	SL("SL", "塞拉利昂", "Sierra Leone", 232), SM("SM", "圣马力诺", "San Marino", 378), SN("SN", "塞内加尔", "Senegal", 221),
	SO("SO", "索马里", "Somalia", 252), SR("SR", "苏里南", "Suriname", 597),
	ST("ST", "圣多美和普林西比", "Sao Tome and Principe", 239), SV("SV", "萨尔瓦多", "El Salvador", 503),
	SY("SY", "叙利亚", "Syria", 963), SZ("SZ", "斯威士兰", "Swaziland", 268), TD("TD", "乍得", "Chad", 235),
	TG("TG", "多哥", "Togo", 228), TH("TH", "泰国", "Thailand", 66), TJ("TJ", "塔吉克斯坦", "Tajikistan", 992),
	TM("TM", "土库曼斯坦", "Turkmenistan", 993), TN("TN", "突尼斯", "Tunisia", 216), TO("TO", "汤加", "Tonga", 676),
	TR("TR", "土耳其", "Turkey", 90), TT("TT", "特立尼达和多巴哥", "Trinidad and Tobago", 1809), TW("TW", "中国台湾省", "Taiwan", 886),
	TZ("TZ", "坦桑尼亚", "Tanzania", 255), UA("UA", "乌克兰", "Ukraine", 380), UG("UG", "乌干达", "Uganda", 256),
	US("US", "美国", "United States", 1), UY("UY", "乌拉圭", "Uruguay", 598),
	UZ("UZ", "乌兹别克斯坦", "Uzbekistan", 233), VC("VC", "圣文森特岛", "Saint Vincent and The Grenadines", 1784), VE("VE", "委内瑞拉", "Venezuela", 58),
	VN("VN", "越南", "Vietnam", 84), YE("YE", "也门", "Yemen", 967), YU("YU", "南斯拉夫", "Yugoslavia", 381),
	ZA("ZA", "南非", "South Africa", 27), ZM("ZM", "赞比亚", "Zambia", 260), ZR("ZR", "扎伊尔", "Zaire", 243),
	ZW("ZW", "津巴布韦", "Zimbabwe", 263),

	//20200909 新增部分国家, 最后数字以10000递增生成
	BT("BT","不丹","bhutan",10000),
	BQ("BQ","博内尔岛、圣尤斯特歇斯岛和萨巴岛","Bonaire; Sint Eustatius; Saba",10001),
	BA("BA","波斯尼亚和黑塞哥维那","bosnia and herzegovina",10002),
	VG("VG","英属维尔京群岛","Virgin Islands (BRITISH)",10003),
	KY("KY","开曼群岛","cayman islands",10004),
	CI("CI","科特迪瓦","Cote D'ivoire",10005),
	HR("HR","克罗地亚","Croatia (LOCAL Name: Hrvatska)",10006),
	DM("DM","多米尼加","dominica",10007),
	TL("TL","东帝汶","Timor-leste",10008),
	FO("FO","法鲁岛","faroe islands",10009),
	GL("GL","格陵兰岛","greenland",10010),
	GG("GG","根西","guernsey",10011),
	MQ("MQ","马提尼克","martinique",10012),
	ME("ME","黑山共和国","montenegro",10013),
	NC("NC","新喀里多尼亚","new caledonia",10014),
	PS("PS","巴勒斯坦","palestinian territory",10015),
	RE("RE","留尼汪岛","Reunion",10016),
	MF("MF","法属圣马丁","saint martin",10017),
	WS("WS","西萨摩亚","samoa",10018),
	RS("RS","塞尔维亚","serbia",10019),
	SX("SX","荷属圣马丁","sint maarten",10020),
	TC("TC","特克斯和凯科斯群岛","turks and caicos islands",10021),
	VU("VU","新赫布里底","vanuatu",10022),
	CV("CV","佛得角","cape verde",10023),
	MR("MR","毛里塔尼亚","mauritania",10024),
	RW("RW","卢旺达","rwanda",10025),
	GQ("GQ","赤道几内亚","equatorial guinea",10026),
	MK("MK","马其顿","macedonia",10027),
	CD("CD","刚果民主共和国","Democratic Republic of Congo",10028),


	AW("AW","阿鲁巴","Aruba",10030),
	IO("IO","英联邦的印度洋领域","British Indian Ocean Territory",10031),
	KM("KM","","Comoros",10032),
	CW("CW","","Curacao",10033),
	VA("VA","梵蒂冈（罗马教庭）","Holy See (VATICAN City State)",10034),
	IM("IM","","Isle of Man",10035),
	JE("JE","","Jersey",10036),
	MH("MH","马绍尔群岛","Marshall Islands",10037),
	PW("PW","帕劳","Palau",10038),
	KN("KN","科摩罗","Saint Kitts and Nevis",10039),
	PM("PM","圣皮埃尔岛及密克隆岛","St. Pierre and Miquelon",10040),
	VI("VI","不列颠岛(美)","Virgin Islands (U.S.)",10041),
	ER("ER","厄立特里亚","Eritrea",10042),
	TV("TV","图瓦卢","Tuvalu",10043),
	AS("AS","美属萨摩亚群岛","American Samoa",10044),
	AX("AX","奥兰群岛","Aland Islands",10045),
	SS("SS","南苏丹共和国","South Sudan",10046),
	FK("FK","马尔维纳斯群岛","Falkland Islands (Malvinas)",10047),
	FM("FM","密克罗尼西亚","Micronesia",10048),
	WF("WF","瓦利斯和富图纳群岛","Wallis and Futuna Islands",10049),
	GP("GP","瓜德鲁普","Guadeloupe",10050),
	GW("GW","几内亚比绍","Guinea-Bissau",10051),
	YT("YT","马约特岛","Mayotte",10052),
	KI("KI","基里巴斯","Kiribati",10053),
	MP("MP","北马里亚纳群岛邦","Northern Mariana Islands",10054),
	NF("NF","诺福克岛","Norfolk Island",10055),
	NU("NU","纽埃","Niue",10056),
	TK("TK","托克劳群岛","Tokelau",10057),

	DEFAULT_EMPTY("", "", "", 1);

	;

	private static final Logger LOGGER = LoggerFactory.getLogger(CountryEnum.class);

	public static CountryEnum fromShortName(String xx) {
		try {
			return CountryEnum.valueOf(xx);
		}catch(Exception ex) {
			LOGGER.warn(ex.getMessage());
			//默认返回南斯拉夫（不能存在的国家）
			return CountryEnum.DEFAULT_EMPTY;
		}
	}


	CountryEnum(String shortName, String fullNameCn, String fullNameEn, int telPrefix) {
		this.shortName = shortName;
		this.fullNameCn = fullNameCn;
		this.fullNameEn = fullNameEn;
		this.telPrefix = telPrefix;
	}



	private String shortName;
	private String fullNameEn;
	private String fullNameCn;
	private int telPrefix;

	private static final Map<String, CountryEnum> countryEnumMap = new ConcurrentHashMap<>();

	static {
		for (CountryEnum s : EnumSet.allOf(CountryEnum.class)) {
			countryEnumMap.put(s.getFullNameEn().toLowerCase().replaceAll(" ",""), s);
		}
	}

	/**
	 * 通过全称获取国家枚举
	 * @param fullNameEn toLowerCase
	 * @return
	 */
	public static CountryEnum getCountryEnumByFullNameEn(String fullNameEn) {
		if(StringUtils.isEmpty(fullNameEn)){
			return CountryEnum.DEFAULT_EMPTY;
		}
		fullNameEn = fullNameEn.toLowerCase().replaceAll(" ","");
		CountryEnum countryEnum = countryEnumMap.get(fullNameEn);
		if(countryEnum == null){
			countryEnum = CountryEnum.DEFAULT_EMPTY;
			LOGGER.warn("### getCountryEnumByFullNameEn no found fullNameEn:{}",fullNameEn);
		}
		return countryEnum;
	}


	/**
	 * 国家代码 两位代码
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @return the fullName
	 */
	public String getFullNameEn() {
		return fullNameEn;
	}

	/**
	 * @return the telPrefix
	 */
	public int getTelPrefix() {
		return telPrefix;
	}

	/**
	 * @return the fullNameCn
	 */
	public String getFullNameCn() {
		return fullNameCn;
	}


}
