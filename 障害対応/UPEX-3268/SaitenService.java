package com.jast.gakuen.up.co.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;

import com.jast.gakuen.framework.GakuenException;
import com.jast.gakuen.framework.db.DbException;
import com.jast.gakuen.framework.util.UtilDate;
import com.jast.gakuen.framework.util.UtilLog;
import com.jast.gakuen.framework.util.UtilProperty;
import com.jast.gakuen.framework.util.UtilStr;
import com.jast.gakuen.system.co.constant.code.SaitenKbn;
import com.jast.gakuen.system.co.constant.code.ShikenUPKbn;
import com.jast.gakuen.system.km.db.entity.KmzHykARComparator;
import com.jast.gakuen.up.UpActionBase;
import com.jast.gakuen.up.co.dao.UPDataAccessObject;
import com.jast.gakuen.up.co.db.dao.CobGaksekiUPDAO;
import com.jast.gakuen.up.co.db.dao.CouParamDAO;
import com.jast.gakuen.up.co.db.entity.CobGaksekiUPAR;
import com.jast.gakuen.up.co.db.entity.CouParamAR;
import com.jast.gakuen.up.co.db.sql.ISQLContents;
import com.jast.gakuen.up.co.db.sql.SQLHelper;
import com.jast.gakuen.up.co.exception.AlreadyUpdateException;
import com.jast.gakuen.up.co.exception.AlreadyUpdatePossibilityException;
import com.jast.gakuen.up.co.exception.BusinessRuleException;
import com.jast.gakuen.up.co.exception.GakuenSystemException;
import com.jast.gakuen.up.co.exception.NoSuchDataException;
import com.jast.gakuen.up.co.mock.SaitenServiceMockDao;
import com.jast.gakuen.up.co.util.DateFactory;
import com.jast.gakuen.up.co.util.dto.GakuseiHeaderDTO;
import com.jast.gakuen.up.co.util.dto.HyokaCondition;
import com.jast.gakuen.up.co.util.dto.HyokaKijunDTO;
import com.jast.gakuen.up.co.util.dto.HyokaKoshinValue;
import com.jast.gakuen.up.co.util.dto.JogaiTaishoIdoKubunCondition;
import com.jast.gakuen.up.co.util.dto.MisaitenJugyoCondition;
import com.jast.gakuen.up.co.util.dto.MisaitenJugyoDTO;
import com.jast.gakuen.up.co.util.dto.SaitenCondition;
import com.jast.gakuen.up.co.util.dto.SaitenIkkatsuDeleteCondition;
import com.jast.gakuen.up.co.util.dto.SaitenIkkatsuDeleteTargetDTO;
import com.jast.gakuen.up.co.util.dto.SaitenStatusDTO;
import com.jast.gakuen.up.co.util.dto.SaitenUnyoCondition;
import com.jast.gakuen.up.co.util.dto.SaitenUnyoDTO;
import com.jast.gakuen.up.co.util.dto.SaitenUnyoHohoDTO;
import com.jast.gakuen.up.co.util.dto.SaitenUnyoListDTO;
import com.jast.gakuen.up.co.util.dto.SaitenUnyoTblCondition;
import com.jast.gakuen.up.co.util.dto.ShikenBetuHyokaKijunCondition;
import com.jast.gakuen.up.co.util.exchanger.HyokaKijunListValueExchanger;
import com.jast.gakuen.up.co.util.exchanger.IdoKubunValueExchanger;
import com.jast.gakuen.up.co.util.exchanger.SaitenIkkatsuDeleteExchanger;
import com.jast.gakuen.up.co.util.exchanger.SaitenUnyoExchanger;
import com.jast.gakuen.up.co.util.exchanger.SaitenValueExchanger;
import com.jast.gakuen.up.co.util.factory.AbstractDynamicSQLContentsFactory;
import com.jast.gakuen.up.km.constant.KmMsgConst;
import com.jast.gakuen.up.km.db.dao.KmcStnTeikiDAO;
import com.jast.gakuen.up.km.db.dao.KmcStnTuisaiDAO;
import com.jast.gakuen.up.km.db.dao.KmcStnUnyoDAO;
import com.jast.gakuen.up.km.db.dao.KmgRisySitnUPDAO;
import com.jast.gakuen.up.km.db.dao.KmgTisiGakUPDAO;
import com.jast.gakuen.up.km.db.dao.KmzHykUPDAO;
import com.jast.gakuen.up.km.db.entity.KmcStnTeikiAR;
import com.jast.gakuen.up.km.db.entity.KmcStnTuisaiAR;
import com.jast.gakuen.up.km.db.entity.KmcStnUnyoAR;
import com.jast.gakuen.up.km.db.entity.KmgRisySitnUPAR;
import com.jast.gakuen.up.km.db.entity.KmgTisiGakUPAR;
import com.jast.gakuen.up.km.db.entity.KmzHykUPAR;

/**
 * 
 * 採点サービス <br>
 * 採点に関する情報と操作を提供します。
 * 
 * @author JApan System Techniques Co.,Ltd.
 */
public class SaitenService extends Service {

// 2007/02/27 不具合管理一覧：No.3621 Start -->>
	/** 素点（追再試評価リミットチェック用） */
	public static final int SOTEN_FROM = 0;				// 素点FROM
	public static final int SOTEN_TO   = 100;				// 素点TO

	/** 結果（追再試評価リミットチェック用） */
	public static final int MAX_SOTEN_CHK_NO_ERR = 0;		// エラーなし
	public static final int MAX_SOTEN_CHK_ERR_1  = 1;		// リミット超過で素点が【SOTEN_TO】以下
	public static final int MAX_SOTEN_CHK_ERR_2  = 2;		// リミット超過で素点が【SOTEN_TO】より大きい
// <<-- End   2007/02/27 不具合管理一覧：No.3621

    private UPDataAccessObject dao;
    
    private final HyokaKijunListValueExchanger
			hyokaKijunListValueExchanger =
					new HyokaKijunListValueExchanger();
    
    /**
     * 
     * @param context
     */
    public SaitenService(Context context) {
        super(context);
    }

    /**
     * 
     * @param base
     */
    public SaitenService(UpActionBase base) {
        super(base);
    }
	
	/**
	 * 
	 * @param service 別インスタンス
	 */
	public SaitenService(Service service) {
		super(service);
	}

    /**
     * 学生の評価基準を取得する
     * 授業ごとの学生の評価基準を取得する(評価方法が評価名称の場合のみ)
     * 
     * @param kanriNo 管理番号
     * @return hyokaKijunList<HyokaKijun> 評価基準リスト
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    public List listHyokaKijun(String kanriNo)  
    	throws NoSuchDataException {
        // 事前条件チェック
        selfTestListHyokaKijunPre(kanriNo);
        // 情報生成
        List hyokaKijunList = listHyokaKijunMaker(kanriNo);
        // 事後条件チェック
        selfTestListHyokaKijunPost(hyokaKijunList);
        return hyokaKijunList;
    }

    /**
     * 
     * 事後条件チェック
     * 
     * @param hyokaKijunList 評価基準リスト
     */
    private void selfTestListHyokaKijunPost(List hyokaKijunList) {
    	
 
    	if (hyokaKijunList == null) {
    		throw new BusinessRuleException("評価基準リストが null");
    	}		

    	int listSize = hyokaKijunList.size();
		if (listSize == 0) {
			throw new BusinessRuleException("評価基準リストが 0件");
		}

		for (int i = 0; i < hyokaKijunList.size(); i++ ) {
			
			if (hyokaKijunList.get(i) == null) {
				throw new BusinessRuleException("評価基準リストの要素が null");
			}
			
			if (!(hyokaKijunList.get(i) instanceof HyokaKijunDTO) ) {
				throw new 
				BusinessRuleException("評価基準リストの要素が評価基準でない");
			}
		}
// 6/25コメント化
//      	/**
//      	 * 評価基準リストのソート順チェック
//      	 */
//		if (hyokaKijunList.size() > 1) {
//			for (int j = 0; j < hyokaKijunList.size() - 1; j++) {
//			    // リストデータが２件以上存在する場合
//		    	HyokaKijunDTO hyokaKjn00 = 
//		    		(HyokaKijunDTO) hyokaKijunList.get(j);
//		    	HyokaKijunDTO hyokaKjn01 = 
//		    		(HyokaKijunDTO) hyokaKijunList.get(j + 1);
//		    	
//		    	String comp1 = null;
//		    	String comp2 = null;
//		    	comp1 = hyokaKjn00.getHyokaCd();
//		    	comp2 = hyokaKjn01.getHyokaCd();
//			    if (comp1.compareTo(comp2) > 0) {
//			    	throw new 
//					BusinessRuleException(
//							"評価基準リストが評価コードの昇順でない");
//			    }    
//			}
//		}
    }

    /**
     * 
     * 情報生成
     * 
     * @param kanriNo 検索条件
     * @return hyokaKijunList<HyokaKijun> 評価基準リスト
     * 
     */
    private List listHyokaKijunMaker(String kanriNo)
    	throws NoSuchDataException {
        // DAOの取得
        final UPDataAccessObject dao = getUPDataAccessObject();
//        UtilLog.debug(this.getClass(),
//        			  "\n--- sqlHelper = [" + this.jg00006SQL + "]");
        // SQLを設定
        dao.setSQL(new SQLHelper("JG00006"));

        // バインド値の設定
        final List listCondiiton = new ArrayList();
        final Date sysdate = UtilDate.cnvSqlDate(DateFactory.getInstance());
        listCondiiton.add(sysdate);
        listCondiiton.add(kanriNo);
        
        try {
	    	final List hyokakijunList = hyokaKijunListValueExchanger.
					listHyokaKijun(getUPDataAccessObject().
							find(listCondiiton));
	    	
	    	return hyokakijunList;
        } finally {
        	//2007-01-17 クローズ漏れ対応
        	dao.clearSQL();
        }
    }

    /**
     * 
     * 事前条件チェック
     * 
     * @param kanriNo 検索条件
     */
    private void selfTestListHyokaKijunPre(String kanriNo) {

        if (kanriNo == null) {
            throw new 
			BusinessRuleException("管理番号に null は設定できません。");
        }
    }

    /**
     * 学生の評価名称値を取得する(追再試)
     * 
     * @param condition 検索条件
     * @return hyokaCd 評価コード
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    public String acquireHyokaMeiTuisaishi(HyokaCondition condition)
    	throws NoSuchDataException {
        // 事前条件チェック
        selfTestAcquireHyokaMeiTuisaishiPre(condition);
        // 情報生成
        String hyokaCd = makeacquireHyokaMeiTuisaishi(condition);
        // 事後チェック
        selfTestAcquireHyokaMeiTuisaishiPost(hyokaCd);
        
        return hyokaCd;
    }

    /**
     * 
     * 情報生成
     * 
     * @param condition 検索条件
     * @return hyokaCd 評価コード
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    private String acquireHyokaMeiTuisaishiMaker(HyokaCondition condition)
    	throws NoSuchDataException {
    	return new SaitenServiceMockDao().acquireHyokaMeiTuisaishiOkNg(condition);
    }

    /**
     * 
     * 情報生成
     * 
     * @param condition 検索条件
     * @return hyokaCd 評価コード
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    private String makeacquireHyokaMeiTuisaishi(HyokaCondition condition)
    	throws NoSuchDataException {
		// 追再試験対象者DAO生成
    	KmgTisiGakUPDAO tisiGakdao = (KmgTisiGakUPDAO) getDbSession().getDao(KmgTisiGakUPDAO.class);
    	try{
        	final KmgTisiGakUPAR tisiGakAR;
    		tisiGakAR = (KmgTisiGakUPAR) tisiGakdao.findByPrimaryKey(
	    			condition.getKaikoNendo().intValue(),
					condition.getGakkiNo().intValue(),
					condition.getJugyoCd(),
					condition.getShikenKbn(),
					condition.getSikenKaisu().intValue(),
					condition.getKanriNo().longValue());

	    	if (tisiGakAR == null) { 
	    		throw new NoSuchDataException("指定したデータが見つかりません。");
	    	}
			
	    	String hyokaCd = tisiGakAR.getHyokaCd();
			if(hyokaCd == null){
				hyokaCd = "";
			}
			
			return hyokaCd;
			
    	}catch (DbException e){
    		throw new GakuenSystemException(e);
    	}
    }

    
    /**
     * 
     * 事前条件チェック
     * 
     * @param condition 検索条件
     * 
     */
    private void selfTestAcquireHyokaMeiTuisaishiPre(HyokaCondition condition) {

        if (condition == null) {
            throw new BusinessRuleException("検索条件に null は設定できません。");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("開講年度に null は設定できません。");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("学期Noに null は設定できません。");
        }

        if (condition.getJugyoCd() == null) {
            throw new BusinessRuleException("授業コードに null は設定できません。");
        }
	
        if (condition.getSikenKaisu() == null) {
            throw new BusinessRuleException("試験回数に null は設定できません。");
        }

        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException("管理番号に null は設定できません。");
        }

        if (!"1".equals(condition.getShikenKbn()) && !"2".equals(condition.getShikenKbn())) {
            throw new BusinessRuleException("試験区分に 1,2以外は設定できません。");
        }
    }
    
    /**
     * 
     * 事後条件チェック
     * 
     * @param condition 検索条件
     * 
     */
    private void selfTestAcquireHyokaMeiTuisaishiPost(String hyokaCd) {

    	if(hyokaCd == null){
    		throw new BusinessRuleException("評価コードに null は設定できません。");
    	}
    }

    /**
     * 
     * 学生の評価名称値を取得する(定期試験のみ)
     * 
     * @param condition 検索条件
     * @return hyokaCd 評価コード
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    public String acquireHyokaMeiTeiki(HyokaCondition condition)
    	throws NoSuchDataException {
        // 事前条件チェック
        selfTestAcquireHyokaMeiTeikiPre(condition);
        // 情報生成
        //String hyokaCd = acquireHyokaMeiTeikiMaker(condition);
        String hyokaCd = makeacquireHyokaMeiTeiki(condition);
        // 事後条件チェック
        selfTestAcquireHyokaMeiTeikiPost(hyokaCd);
        
        return hyokaCd;
    }

    /**
     * 
     * 情報生成
     * 
     * @param condition 検索条件
     * @return hyokaCd 評価コード
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    private String acquireHyokaMeiTeikiMaker(HyokaCondition condition)
    	throws NoSuchDataException {
    	return new SaitenServiceMockDao().acquireHyokaMeiTeikiOkNg(condition);
    }

    /**
     * 
     * 情報生成
     * 
     * @param condition 検索条件
     * @return hyokaCd 評価コード
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    private String makeacquireHyokaMeiTeiki(HyokaCondition condition)
    	throws NoSuchDataException {
		// 履修採点DAO生成
    	KmgRisySitnUPDAO rsyStndAO = (KmgRisySitnUPDAO) getDbSession().getDao(KmgRisySitnUPDAO.class);
    	try{
        	final KmgRisySitnUPAR rsyStnAR;
    		int shikenKsu = 0;
    		if (condition.getSikenKaisu() == null) {
    			shikenKsu = 1;
    		} else {
    			shikenKsu = condition.getSikenKaisu().intValue();
    		}
        	rsyStnAR = (KmgRisySitnUPAR) rsyStndAO.findByPrimaryKey(
	    			condition.getKaikoNendo().intValue(),
					condition.getGakkiNo().intValue(),
					condition.getJugyoCd(),
					shikenKsu,
					condition.getKanriNo().longValue());

	    	if (rsyStnAR == null) { 
	    		throw new NoSuchDataException("指定したデータが見つかりません。");
	    	}
			
	    	String hyokaCd = rsyStnAR.getHyokaCd();
			if(hyokaCd == null){
				hyokaCd = "";
			}
			
			return hyokaCd;
			
    	}catch (DbException e){
    		throw new GakuenSystemException(e);
    	}
    }

    /**
     * 
     * 事前条件チェック
     * 
     * @param condition 検索条件
     * 
     */
    private void selfTestAcquireHyokaMeiTeikiPre(HyokaCondition condition) {
    	
        if (condition == null) {
            throw new BusinessRuleException("検索条件に null は設定できません。");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("開講年度に null は設定できません。");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("学期Noに null は設定できません。");
        }

        if (condition.getJugyoCd() == null) {
            throw new BusinessRuleException("授業コードに null は設定できません。");
        }

        // TODO 定期試験には試験回数不要
//        if (condition.getSikenKaisu() == null) {
//            throw new BusinessRuleException("試験回数に null は設定できません。");
//        }

        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException("管理番号に null は設定できません。");
        }

        if (!"0".equals(condition.getShikenKbn())) {
            throw new BusinessRuleException("試験区分に 0以外は設定できません。");
        }
    }
    
    /**
     * 
     * 事後条件チェック
     * 
     * @param condition 検索条件
     * 
     */
    private void selfTestAcquireHyokaMeiTeikiPost(String hyokaCd) {

    	if(hyokaCd == null){
    		throw new BusinessRuleException("評価コードに null は設定できません。");
    	}
    }

    /**
     * 
     * 授業ごとの学生の素点を取得する(追再試)
     * 
     * @param condition 検索条件
     * @return hyokaKijunList<HyokaKijun> 評価基準
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    public String acquireHyokaSotenTsuisaishi(HyokaCondition condition)
    	throws NoSuchDataException {
        // 事前条件チェック
        selfTestAcquireHyokaSotenTsuisaishiPre(condition);
        // 情報生成
        String soten = makeacquireHyokaSotenTuisaishi(condition);
        // 事後チェック
        selfTestAcquireHyokaSotenTuisaishiPost(soten);

        return soten;
    }

    /**
     * 
     * 情報生成
     * 
     * @param condition 検索条件
     * @return soten 素点
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    private String acquireHyokaSotenTsuisaishiMaker(HyokaCondition condition)
    	throws NoSuchDataException {
    	return new SaitenServiceMockDao().acquireHyokaSotenTsuisaishiOkNg(condition);
    }

    /**
     * 
     * 情報生成
     * 
     * @param condition 検索条件
     * @return hyokaCd 評価コード
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    private String makeacquireHyokaSotenTuisaishi(HyokaCondition condition)
    	throws NoSuchDataException {
		// 追再試験対象者DAO生成
    	KmgTisiGakUPDAO tisiGakdao = (KmgTisiGakUPDAO) getDbSession().getDao(KmgTisiGakUPDAO.class);
    	try{
        	final KmgTisiGakUPAR tisiGakAR;
    		tisiGakAR = (KmgTisiGakUPAR) tisiGakdao.findByPrimaryKey(
	    			condition.getKaikoNendo().intValue(),
					condition.getGakkiNo().intValue(),
					condition.getJugyoCd(),
					condition.getShikenKbn(),
					condition.getSikenKaisu().intValue(),
					condition.getKanriNo().longValue());

	    	if (tisiGakAR == null) { 
	    		throw new NoSuchDataException("指定したデータが見つかりません。");
	    	}
			
	    	// 6/25 修正St↓↓↓↓↓↓↓↓↓↓↓
	    	//	    	String soten = tisiGakAR.getHyokaTen().toString();
			//	    	
			//	    	if(soten == null){
			//				soten = "";
			//			}
//	    	String soten = "";
	    	final Integer hyokaTen = tisiGakAR.getHyokaTen();
	    	final String hyokaCd = tisiGakAR.getHyokaCd();
//	    	if (tisiGakAR.getHyokaTen() != null ){
//		    	soten = String.valueOf(tisiGakAR.getHyokaTen());
//	    	}
	    	// 6/25 修正En↑↑↑↑↑↑↑↑↑↑↑
	    	if(hyokaCd != null && hyokaTen ==null){
	    		return hyokaCd;
	    	}
	    	final String soten = hyokaTen != null?hyokaTen.toString():"";
			return soten;
			
    	}catch (DbException e){
    		throw new GakuenSystemException(e);
    	}
    }

    /**
     * 
     * 事前条件チェック
     * 
     * @param condition 検索条件
     * 
     */
    private void selfTestAcquireHyokaSotenTsuisaishiPre(HyokaCondition condition) {
    	
        if (condition == null) {
            throw new BusinessRuleException("検索条件に null は設定できません。");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("開講年度に null は設定できません。");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("学期Noに null は設定できません。");
        }

        if (condition.getJugyoCd() == null) {
            throw new BusinessRuleException("授業コードに null は設定できません。");
        }
	
        if (condition.getSikenKaisu() == null) {
            throw new BusinessRuleException("試験回数に null は設定できません。");
        }

        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException("管理番号に null は設定できません。");
        }

        if (!("1".equals(condition.getShikenKbn()) 
        		|| "2".equals(condition.getShikenKbn()))) {
            throw new BusinessRuleException("試験区分に 1,2以外は設定できません。");
        }
    }
    
    /**
     * 
     * 事後チェック
     * 
     * @param condition 検索条件
     * 
     */
    private void selfTestAcquireHyokaSotenTuisaishiPost(String soten) {

    	if(soten == null){
    		throw new BusinessRuleException("素点に null は設定できません。");
    	}
    }

    /**
     * 
     * 授業ごとの学生の素点を取得する(定期試験のみ)
     * 
     * @param condition 検索条件
     * @return soten 素点
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    public String acquireHyokaSotenTeiki(HyokaCondition condition)
    	throws NoSuchDataException {
        // 事前条件チェック
        selfTestAcquireHyokaSotenTeikiPre(condition);
        // 情報生成
        String soten = makeacquireHyokaSotenTeiki(condition);
        // 事後条件チェック
        selfTestAcquireHyokaSotenTeikiPost(soten);

        return soten;
    }

    /**
     * 
     * 情報生成
     * 
     * @param condition 検索条件
     * @return soten 素点
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    private String acquireHyokaSotenTeikiMaker(HyokaCondition condition)
    	throws NoSuchDataException {
    	return new SaitenServiceMockDao().acquireHyokaSotenTeikiOkNg(condition);
    }

    /**
     * 
     * 情報生成
     * 
     * @param condition 検索条件
     * @return soten 素点
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    private String makeacquireHyokaSotenTeiki(HyokaCondition condition)
    	throws NoSuchDataException {
		// 履修採点DAO生成
    	KmgRisySitnUPDAO rsyStndAO = (KmgRisySitnUPDAO) getDbSession().getDao(KmgRisySitnUPDAO.class);
    	try{
    		
    		int shikenKaisu = 0;
    		if ("0".equals(condition.getShikenKbn())) {
    			// 定期試験の場合は試験回数を１固定
    			shikenKaisu = 1;
    		}
    		
        	final KmgRisySitnUPAR rsyStnAR;
        	rsyStnAR = (KmgRisySitnUPAR) rsyStndAO.findByPrimaryKey(
	    			condition.getKaikoNendo().intValue(),
					condition.getGakkiNo().intValue(),
					condition.getJugyoCd(),
					shikenKaisu,
					condition.getKanriNo().longValue());

	    	if (rsyStnAR == null) { 
	    		throw new NoSuchDataException("指定したデータが見つかりません。");
	    	}
			
	    	final Integer hyokaTen = rsyStnAR.getHyokaTen();
	    	final String hyokaCd =rsyStnAR.getHyokaCd();
	    	if(hyokaCd != null && hyokaTen ==null){
	    		return hyokaCd;
	    	}
	    	final String soten = hyokaTen != null?hyokaTen.toString():"";
			
			return soten;
			
    	}catch (DbException e){
    		throw new GakuenSystemException(e);
    	}
    }
    
    /**
     * 
     * 事前条件チェック
     * 
     * @param condition 検索条件
     * 
     */
    private void selfTestAcquireHyokaSotenTeikiPre(HyokaCondition condition) {
    	
        if (condition == null) {
            throw new BusinessRuleException("検索条件に null は設定できません。");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("開講年度に null は設定できません。");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("学期Noに null は設定できません。");
        }

        if (condition.getJugyoCd() == null) {
            throw new BusinessRuleException("授業コードに null は設定できません。");
        }
	// TODO 定期試験には試験回数不要
//        if (condition.getSikenKaisu() == null) {
//            throw new BusinessRuleException("試験回数に null は設定できません。");
//        }

        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException("管理番号に null は設定できません。");
        }

        if (!"0".equals(condition.getShikenKbn())) {
            throw new BusinessRuleException("試験区分に 0以外は設定できません。");
        }
    }
    
    /**
     * 
     * 事後チェック
     * 
     * @param condition 検索条件
     * 
     */
    private void selfTestAcquireHyokaSotenTeikiPost(String soten) {

    	if(soten == null){
    		throw new BusinessRuleException("素点に null は設定できません。");
    	}
    }

	/**
	 * 
	 * 学生の素点または評価名称値を更新する。(追再試の場合)
	 * 
	 * @param hyokaKoshinValue 設定情報
	 * @throws AlreadyUpdatePossibilityException
	 * @throws AlreadyUpdateException
	 * @throws NoSuchDataException
	 */
	 public void updateTuisaishi(HyokaKoshinValue value) 
	 	throws AlreadyUpdatePossibilityException, 
		AlreadyUpdateException, 
		NoSuchDataException {
	    
	    // 事前条件チェック
	    selfTestUpdateTuisaishiPre(value);
	    try {
			// 追再試験対象者TBL更新
			updateKmgTisiGak(value);
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		} catch (GakuenException e) {
			throw new GakuenSystemException(e);
		}
	}

	/**
	 * 
	 * 追再試験対象者TBLを更新する
	 * 
	 * @param hyokaKoshinValue 設定情報
	 * @throws AlreadyUpdatePossibilityException 
	 * 既に更新されている可能性がある場合の例外
	 * @throws AlreadyUpdateException 既にデータが更新されている場合の例外
	 * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
	 * @throws DbException
	 * @throws GakuenException
	 */
    private void updateKmgTisiGak(HyokaKoshinValue value) 
    	throws AlreadyUpdatePossibilityException,
		AlreadyUpdateException,
		NoSuchDataException, DbException, GakuenException {

		// 追再試験対象者DAO生成
    	KmgTisiGakUPDAO tisiGakDAO = 
    		(KmgTisiGakUPDAO) getDbSession().getDao(KmgTisiGakUPDAO.class);
	    try {
	    	KmgTisiGakUPAR ar = (KmgTisiGakUPAR) tisiGakDAO.findByPrimaryKey(
					value.getKaikoNendo().intValue(),
					value.getGakkiNo().intValue(),
					value.getJugyoCd(),
					value.getTuisaisikenKbn(),
					value.getSikenKaisu().intValue(),
					value.getKanriNo().longValue());

	    	if (ar == null){
	    		throw new NoSuchDataException("指定されたデータが見つかりません。");
	    	}

	    	String unyoHoho = value.getSaitenKbn(); // 素点
	    	
	    	// 素点運用の場合
	    	if (unyoHoho.equals("1")) {
	    		// 評価点を設定
	    		ar.setHyokaTen(value.getHyokaTen());
	    		
	    		if (ar.getHyokaCd() != null
	    				&& !"".equals(ar.getHyokaCd())) {
	    			// 評価コードが設定されている場合、
	    			// かつ、素点が設定されている場合は、評価コードをクリアする。
	    			if (value.getHyokaTen() != null) {
	    	    		ar.setHyokaCd(null);
	    			}
	    		}
	    	}
	    	
	    	// 評価名称運用の場合
	    	if (unyoHoho.equals("2")) {
	    		// 評価コードを設定
	    		ar.setHyokaCd(value.getHyokaCd());
	    		
	    		if (ar.getHyokaTen() != null) {
	    			// 素点が設定されている場合、
	    			// かつ、評価コードが設定されている場合は、素点をクリアする。
	    			if (value.getHyokaCd() != null
		    				&& !"".equals(value.getHyokaCd())) {
	    	    		ar.setHyokaTen(null);
	    			}
	    		}
	    	}
	    	
//	    	// 採点更新者
//	    	ar.setSitnJinjiCd(value.getSitnJinjiCd());
//	    	
//	    	// 採点更新日
//	    	ar.setSitnUpdateDate(
//	    			UtilDate.cnvSqlDate(value.getSitnUpdateDate()));

			// 追再試験対象者TBLを更新
	    	ar.store();
	    	
    	}catch (DbException e){
    		throw new GakuenSystemException(e);
    	}
    }

    /**
     * 
     *  事前条件チェック
     * 
     * @param hyokaKoshinValue 設定情報
     */
    private void selfTestUpdateTuisaishiPre(HyokaKoshinValue value) {
    	
        if (value == null) {
            throw new BusinessRuleException("登録条件に null は設定できません。");
        }
        
        if (value.getKaikoNendo() == null) {
            throw new BusinessRuleException("開講年度に null は設定できません。");
        }

        if (value.getGakkiNo() == null) {
            throw new BusinessRuleException("学期Noに null は設定できません。");
        }

        if (value.getJugyoCd() == null) {
            throw new BusinessRuleException("授業コードに null は設定できません。");
        }

        if (value.getTuisaisikenKbn() == null) {
            throw new BusinessRuleException("追再試験区分に null は設定できません。");
        }
        
        if (!"1".equals(value.getTuisaisikenKbn()) && !"2".equals(value.getTuisaisikenKbn())) {
            throw new BusinessRuleException("追再試験区分に 1,2以外は設定できません。");
        }
        
    	if (value.getSikenKaisu() == null) {
    		throw new BusinessRuleException("試験回数に null は設定できません。");
    	}

        if (value.getKanriNo() == null) {
            throw new BusinessRuleException("管理番号に null は設定できません。");
        }
        
        if (value.getSitnJinjiCd() == null) {
            throw new BusinessRuleException("採点更新者に null は設定できません。");
        }
        
        if (value.getSitnUpdateDate() == null) {
            throw new BusinessRuleException("採点更新日に null は設定できません。");
        }
        
//    	String unyoHoho = value.getSaitenKbn();
//    	
//    	// 素点運用の場合
//    	if (unyoHoho.equals("1")) {
//    		if (value.getHyokaTen() == null) {
//    			throw new BusinessRuleException("評価点に null は設定できません。");
//    		}
//    	}
//    	
//    	// 評価名称運用の場合
//    	if (unyoHoho.equals("2")) {
//    		if (value.getHyokaCd() == null) {
//    			throw new BusinessRuleException("評価コードに null は設定できません。");
//    		}
//    	}	
    }
    
	/**
	 * 
	 * 学生の素点または評価名称値を更新する。(定期試験の場合)
	 * 
	 * @param hyokaKoshinValue 設定情報
	 * @throws NoSuchDataException
	 * @throws AlreadyUpdatePossibilityException
	 * @throws AlreadyUpdateException
	 * 
	*/
	public void updateTeiki(HyokaKoshinValue value) 
		throws NoSuchDataException, 
		AlreadyUpdatePossibilityException, 
		AlreadyUpdateException {
	    
	    // 事前条件チェック
	    selfTestUpdateTeikiPre(value);
	    try {
			// 情報更新
			updateKmgRisySitn(value);
		} catch (GakuenException e) {
			throw new GakuenSystemException(e);
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		}
	}

	/**
	 * 
	 * 履修採点TBLを更新する
	 * 
	 * @param value 設定情報
	 * @throws AlreadyUpdatePossibilityException 
	 * 既に更新されている可能性がある場合の例外
	 * @throws AlreadyUpdateException 既にデータが更新されている場合の例外
	 * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
	 * @throws GakuenException
	 * @throws DbException
	 * 
	 */
    private void updateKmgRisySitn(HyokaKoshinValue value) 
    	throws NoSuchDataException,
        AlreadyUpdatePossibilityException,
		AlreadyUpdateException, GakuenException, DbException {
		// 履修採点DAO生成
    	final KmgRisySitnUPDAO rsyStnDAO =
    		(KmgRisySitnUPDAO) getDbSession().getDao(KmgRisySitnUPDAO.class);
    	try {
    		final KmgRisySitnUPAR ar =
    			(KmgRisySitnUPAR) rsyStnDAO.findByPrimaryKey(
					value.getKaikoNendo().intValue(),
					value.getGakkiNo().intValue(),
					value.getJugyoCd(),
					value.getSikenKaisu() != null?
							value.getSikenKaisu().intValue():1,
					value.getKanriNo().longValue());

	    	if (ar == null){
	    		throw new NoSuchDataException("指定されたデータが見つかりません。");
	    	}

	    	final String unyoHoho = value.getSaitenKbn();
	    	// 素点運用の場合
	    	if (unyoHoho.equals(String.valueOf(SaitenKbn.SOTEN.getCode()))) {
	    		// 評価点を設定
	    		ar.setHyokaTen(value.getHyokaTen());
	    		if (ar.getHyokaCd() != null
	    				&& !"".equals(ar.getHyokaCd())) {
	    			// 評価コードが設定されている場合、
	    			// かつ、素点が設定されている場合は、評価コードをクリアする。
	    			if (value.getHyokaTen() != null) {
	    	    		ar.setHyokaCd(null);
	    			}
	    		}
	    	}
	    	// 評価名称運用の場合
	    	if (unyoHoho.equals(String.valueOf(
	    			SaitenKbn.HYOKANAME.getCode()))) {
	    		// 評価コードを設定
	    		ar.setHyokaCd(value.getHyokaCd());
	    		if (ar.getHyokaTen() != null) {
	    			// 素点が設定されている場合、
	    			// かつ、評価コードが設定されている場合は、素点をクリアする。
	    			if (value.getHyokaCd() != null
		    				&& !"".equals(value.getHyokaCd())) {
	    	    		ar.setHyokaTen(null);
	    			}
	    		}
	    	}
	    	
//	    	// 採点更新者
//	    	ar.setSitnJinjiCd(value.getSitnJinjiCd());
//	    	
//	    	// 採点更新日
//	    	ar.setSitnUpdateDate(
//	    			UtilDate.cnvSqlDate(value.getSitnUpdateDate()));

			// 追再試験対象者TBLを更新
	    	ar.store();
	    	
    	}catch (DbException e){
    		throw new GakuenSystemException(e);
    	}
    }

    /**
     * 
     *  事前条件チェック
     * 
     * @param hyokaKoshinValue 設定情報
     */
    private void selfTestUpdateTeikiPre(HyokaKoshinValue value) {

    	if (value == null) {
    		throw new BusinessRuleException("登録条件に null は設定できません。");
    	}
       
    	if (value.getKaikoNendo() == null) {
    		throw new BusinessRuleException("開講年度に null は設定できません。");
    	}

    	if (value.getGakkiNo() == null) {
    		throw new BusinessRuleException("学期Noに null は設定できません。");
    	}

    	if (value.getJugyoCd() == null) {
    		throw new BusinessRuleException("授業コードに null は設定できません。");
    	}
    	
//    	if (value.getSikenKaisu() == null) {
//    		throw new BusinessRuleException("試験回数に null は設定できません。");
//    	}

    	if (value.getKanriNo() == null) {
    		throw new BusinessRuleException("管理番号に null は設定できません。");
    	}
       
    	if (value.getSitnJinjiCd() == null) {
    		throw new BusinessRuleException("採点更新者に null は設定できません。");
    	}
       
    	if (value.getSitnUpdateDate() == null) {
    		throw new BusinessRuleException("採点更新日に null は設定できません。");
    	}
    	
//    	String unyoHoho = value.getSaitenKbn();
//    	
//    	// 素点運用の場合
//    	if (unyoHoho.equals("1")) {
//    		if (value.getHyokaTen() == null) {
//    			throw new BusinessRuleException("評価点に null は設定できません。");
//    		}
//    	}
//    	
//    	// 評価名称運用の場合
//    	if (unyoHoho.equals("2")) {
//    		if (value.getHyokaCd() == null) {
//    			throw new BusinessRuleException("評価コードに null は設定できません。");
//    		}
//    	}	    	
    }
    

    /**
     * 指定した採点(追再試験)をCSV出力済にする
     * @param dto
     */
    public void updateTsusaishikenCsvOut(SaitenStatusDTO dto) {
    	
        //事前条件チェック
        selfTestTsusaishikenCsvOutPre(dto);

        // 情報生成
        updateTsusaishiken(dto);
    }

    /**
     * データ更新
     * 指定した採点(追再試験)をCSV出力済にする
     * @param dto
     */
    private void updateTsusaishiken(SaitenStatusDTO dto) {
    	
		// 採点登録状況＿定期試験のDAO生成
    	KmcStnTuisaiDAO stnTuisaiDAO =
    		(KmcStnTuisaiDAO) getDbSession().getDao(KmcStnTuisaiDAO.class);
    	KmcStnTuisaiAR stnTuisaiAR = null;

    	try {
    		
    		stnTuisaiAR = stnTuisaiDAO.findByPrimaryKey(
	        				dto.getNendo().intValue(),
	        				dto.getGakkiNo().intValue(),
	        				dto.getJugyoCode(),
	        				dto.getShikenKbn(),
	        				dto.getShikenKaisu().intValue(),
	        				dto.getKanriNo().longValue()
							);
    		
    		if (stnTuisaiAR != null) {
    			
    			// TODO 定数クラスを利用するべし
    			// 検索対象があった場合は更新
    			stnTuisaiAR.setCsvOutputFlg(1);
    		} else {
    			// 検索対象がない場合は新規追加
    			stnTuisaiAR =
    				new KmcStnTuisaiAR(
        					getDbSession(),
        					dto.getNendo().intValue(),
        					dto.getGakkiNo().intValue(),
        					dto.getJugyoCode(),
        					dto.getShikenKbn(),
        					dto.getShikenKaisu().intValue(),
        					dto.getKanriNo().longValue()
							);
    			
    			stnTuisaiAR.setCsvOutputFlg(1);
    		}
    		
    		// 採点更新者
    		stnTuisaiAR.setSitnJinjiCd(getDbSession().getUserId());
    		
    		// 採点更新日
    		stnTuisaiAR.setSitnUpdateDate(getDbSession().getCurrentTime());
    		
    		
    		stnTuisaiAR.store();
// ▽▽ UPEX-1199対応 2010.1.28 h.matsuda add start
    		// メモリ消費を抑える為、キャッシュさせない
    		stnTuisaiDAO.destroy();    		
// △△ UPEX-1199対応 2010.1.28 h.matsuda add end 
    		
    	} catch (DbException dbe) {
   			throw new GakuenSystemException(
   					"updateTsusaishiken KmcStnTuisaiAR", dbe);
   			
   		} catch (GakuenException ge) {
   			throw new GakuenSystemException(
   					"updateTsusaishiken KmcStnTuisaiAR", ge);
   		}
        
    }

    /**
     * 事前条件
     * 「指定した採点(追再試験)をCSV出力済にする」の検索条件をチェックする
     * @param dto
     */
    private void selfTestTsusaishikenCsvOutPre(SaitenStatusDTO dto) {

        if (dto == null) {
            throw
			new BusinessRuleException("採点状況に null は設定できません。");
        }
        if (dto.getNendo() == null) {
            throw
			new BusinessRuleException("年度に null は設定できません。");
        }
        if (dto.getGakkiNo() == null) {
            throw
			new BusinessRuleException("学期Ｎｏに null は設定できません。");
        }
        if (dto.getJugyoCode() == null) {
            throw
			new BusinessRuleException("授業コードに null は設定できません。");
        }
        if (dto.getKanriNo() == null) {
            throw
			new BusinessRuleException("管理Ｎｏに null は設定できません。");
        }
        if (dto.getShikenKaisu() == null) {
            throw
			new BusinessRuleException("試験回数に null は設定できません。");
        }
        if (dto.getShikenKbn() == null) {
            throw
			new BusinessRuleException("試験区分に null は設定できません。");
        }           
        

    }

    /**
     * 指定した採点(定期試験)をCSV出力済にする
     * @param list
     */
    public void updateTeikiShikenCsvOut(SaitenStatusDTO dto) {

        //事前条件チェック
        selfTestTeikiShikenCsvOutPre(dto);

        // 情報生成
        updateTeikiShiken(dto);
    }

    /**
     * 情報の更新
     * @param list
     */
    private void updateTeikiShiken(SaitenStatusDTO dto) {

    	   	
		// 採点登録状況＿定期試験のDAO生成
    	KmcStnTeikiDAO stnTeikiDAO =
    		(KmcStnTeikiDAO) getDbSession().getDao(KmcStnTeikiDAO.class);
    	KmcStnTeikiAR stnTeikiAR = null;

    	try {
    		
    		int shikenKsu = 0;
    		if (dto.getShikenKaisu() == null) {
    			shikenKsu = 1;
    		} else {
    			shikenKsu = dto.getShikenKaisu().intValue();
    		}
    		stnTeikiAR = stnTeikiDAO.findByPrimaryKey(
		    				dto.getNendo().intValue(),
		    				dto.getGakkiNo().intValue(),
		    				dto.getJugyoCode(),
		    				dto.getShikenKbn(),
		    				shikenKsu,
		    				dto.getKanriNo().longValue()
							);

    		if (stnTeikiAR != null ) {
    			// 既存データがある場合は更新
    			// TODO 定数クラスに置き換え
    			stnTeikiAR.setCsvOutputFlg(1);				
    			
    		} else {
    			// 検索対象がなかったため新規追加
    			stnTeikiAR =
    				new KmcStnTeikiAR(
    						getDbSession(),
    						dto.getNendo().intValue(),
    						dto.getGakkiNo().intValue(),
    						dto.getJugyoCode(),
		    				dto.getShikenKbn(),
		    				shikenKsu,
		    				dto.getKanriNo().longValue()
							);
    			
    			//TODO 定数クラスに置き換え
    			stnTeikiAR.setCsvOutputFlg(1);    			
    		}
    		
    		// 採点更新者
    		stnTeikiAR.setSitnJinjiCd(getDbSession().getUserId());
    		
    		// 採点更新日
    		stnTeikiAR.setSitnUpdateDate(getDbSession().getCurrentTime());
    		
    		stnTeikiAR.store();
// ▽▽ UPEX-1199対応 2010.1.28 h.matsuda add start
    		// メモリ消費を抑える為、キャッシュさせない
    		stnTeikiDAO.destroy();    		
// △△ UPEX-1199対応 2010.1.28 h.matsuda add end    		
   		} catch (DbException dbe) {
   			throw new GakuenSystemException(
   					"updateTeikiShiken KmcStnTeikiAR", dbe);
   			
   		} catch (GakuenException ge) {
   			throw new GakuenSystemException(
   					"updateTeikiShiken KmcStnTeikiAR", ge);
   		}
	
    }

    /**
     * 事前条件　
     * 「指定した採点(定期試験)をCSV出力済にする」の検索条件をチェックする　
     * @param list
     */
    private void selfTestTeikiShikenCsvOutPre(SaitenStatusDTO dto) {
        if (dto == null) {
            throw new BusinessRuleException(
            		"採点状況に null は設定できません。");
        }

        if (dto.getNendo() == null) {
            throw 
			new BusinessRuleException("年度に null は設定できません。");
        }
        if (dto.getGakkiNo() == null) {
            throw
			new BusinessRuleException("学期Ｎｏに null は設定できません。");
        }
        if (dto.getJugyoCode() == null) {
            throw
			new BusinessRuleException("授業コードに null は設定できません。");
        }
        if (dto.getKanriNo() == null) {
            throw
			new BusinessRuleException("管理Ｎｏに null は設定できません。");
        }
//        if (dto.getShikenKaisu() == null) {
//            throw
//			new BusinessRuleException("試験回数に null は設定できません。");
//        }
        if (dto.getShikenKbn() == null) {
            throw
			new BusinessRuleException("試験区分に null は設定できません。");
        }
    }

    /**
     * @param condition
     * @param saitenUnyo
     * @return
     * @throws NoSuchDataException
     */
    public SaitenUnyoDTO acquireSaitenUnyo(SaitenUnyoTblCondition condition) throws NoSuchDataException {
        //事前条件チェック
        selfTestAcquireSaitenUnyoPre(condition);

        // 情報生成
        SaitenUnyoDTO saitenUnyo = makeSaitenUnyo(condition);

        //事後条件チェック
        selfTestAcquireSaitenUnyoPost(saitenUnyo);

        return saitenUnyo;
    }

    /**
     * @param saitenUnyo
     */
    private void selfTestAcquireSaitenUnyoPost(SaitenUnyoDTO saitenUnyo) {
        if (saitenUnyo == null) {
            throw new BusinessRuleException("採点運用に null は設定できません。");
        }
    	if (saitenUnyo.getGakkiNo() == null) {
    		throw new BusinessRuleException("採点運用の学期ＮＯに null は設定できません。");
    	}
    	if (saitenUnyo.getKaikoNendo() == null) {				 
    		throw new BusinessRuleException("採点運用の開講年度に null は設定できません。");
    	}
    	if (saitenUnyo.getKanriBsyoCd() == null) {				 
    		throw new BusinessRuleException("採点運用の管理部署コードに null は設定できません。");
    	}			   											 
    	if (saitenUnyo.getSaitenKaishibi() == null) {			 
    		throw new BusinessRuleException("採点運用の採点開始日時に null は設定できません。");
    	}			   											 
    	if (saitenUnyo.getSaitenKbn() == null) {				 
    		throw new BusinessRuleException("採点運用の採点方法区分に null は設定できません。");
    	}			   											 
    	if (saitenUnyo.getSaitenShuryobi() == null) {			 
    		throw new BusinessRuleException("採点運用の採点終了日時に null は設定できません。");
    	}			   											 
    	if (saitenUnyo.getShikenKbn() == null) {				 
    		throw new BusinessRuleException("採点運用の試験区分に null は設定できません。");
    	}
    }

    /**
     * @param condition
     * @throws NoSuchDataException
     */
    private SaitenUnyoDTO makeSaitenUnyo(SaitenUnyoTblCondition condition) throws NoSuchDataException {
        
      // SQLを設定
      final SQLHelper sqlHelper = new SQLHelper(ISQLContents.ID_KM + "00043");

      // SQLを設定 採点状況の一覧(定期試験)を取得
      getUPDataAccessObject().setSQL(sqlHelper);
      // データ変換オブジェクトを生成
      final SaitenUnyoExchanger exchanger = new SaitenUnyoExchanger();

      try {
	      return exchanger.acquireSaitenUnyo(getUPDataAccessObject().find(
	              exchanger.preparedStatementBindingValues(condition)));
      } finally {
    	//2007-01-17 クローズ漏れ対応
      	getUPDataAccessObject().clearSQL();
      }

    }

    /**
     * @param condition
     */
    private void selfTestAcquireSaitenUnyoPre(SaitenUnyoTblCondition condition) {
        if (condition == null) {
            throw new BusinessRuleException("検索条件に null は設定できません。");
        }
        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("開講年度に null は設定できません。");
        }
        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("学期Ｎｏに null は設定できません。");
        }
        if (condition.getJugyoCd() == null) {
            throw new BusinessRuleException("授業コードに null は設定できません。");
        }
        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException("試験区分に null は設定できません。");
        }
    }

    /**
     *  事前条件チェック
     * <BR>
     * @param condition 
     * 
     */
    private void selfTestListSaitenUnyouPre(SaitenCondition condition) {
    	
    	if (condition == null) {
    		throw new BusinessRuleException("conditionがnull エラー");
    	}
    	
    	if (condition.getNendo() == null) {
    		throw new BusinessRuleException("nendoがnull エラー");
    	}
    	
    	if (condition.getGakkiNo() == null) {
    		throw new BusinessRuleException("gakkiNoがnull エラー");
    	}
    }
    
    /**
     * 採点運用方法の一覧を取得する
     * <BR>
     * @param condition
     * @throws NoSuchDataException
     *  指定したデータが見つからなかった場合の例外
     * @return List 
     * 
     */
    public List listSaitenUnyou(SaitenCondition condition) 
    	throws NoSuchDataException {
 
    	// 事前条件
    	selfTestListSaitenUnyouPre(condition);
    	// 情報生成
    	List list = makeListSaitenUnyou(condition);
    	// 事後条件
    	selfTestListSaitenUnyouPost(list);
    	
        return list;
    }
    
	/**
	 * 
	 * 情報生成
	 * 
	 * @param SaitenCondition 
	 * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
	 */
    private List makeListSaitenUnyou(SaitenCondition condition) 
    	throws NoSuchDataException {
        // DAOの取得
        final UPDataAccessObject dao = getUPDataAccessObject();

        // SQLを設定
        final SQLHelper sqlHelper = new SQLHelper(ISQLContents.ID_KM + "00108");

        
        UtilLog.debug(this.getClass(),
        			  "\n--- sqlHelper = [" + sqlHelper + "]");
        // SQLを設定
        dao.setSQL(sqlHelper);


        // データ変換オブジェクトを生成
        final SaitenUnyoExchanger exchanger = new SaitenUnyoExchanger();

        try {
	        return exchanger.listSaitenUnyou(getUPDataAccessObject().find(
	                exchanger.preparedStatementBindingValues(condition)));
        } finally {
        	//2007-01-17 クローズ漏れ対応
        	dao.clearSQL();
        }
    }
    
    /**
     * 未採点教員の一覧を取得する
     * <BR>
     * @param condition
     * @throws NoSuchDataException
     *  指定したデータが見つからなかった場合の例外
     * @return List 
     * 
     */
    public List acquireMiSaitenKyoinList(MisaitenJugyoCondition condition)
    	throws NoSuchDataException {
 
    	// 事前条件
    	selfTestListMiSaitenKyoinPre(condition);
    	// 情報生成
    	List list = makeListMiSaitenKyoin(condition);
    	// 事後条件
    	selfTestListMiSaitenKyoinPost(list);
    	
        return list;
    }
    
    /**
     *  事前条件チェック
     * <BR>
     * @param condition 
     * 
     */
    private void selfTestListMiSaitenKyoinPre(MisaitenJugyoCondition condition) {
    	
    	if (condition == null) {
    		throw new BusinessRuleException("検索条件がnull エラー");
    	}
    	
    	if (condition.getNendo() == null) {
    		throw new BusinessRuleException("年度がnull エラー");
    	}
    	
    	if (condition.getGakkiNo() == null) {
    		throw new BusinessRuleException("学期Ｎｏがnull エラー");
    	}

    	if (condition.getKanriBusyoCode() == null) {
    		throw new BusinessRuleException("管理部署コードがnull エラー");
    	}
    }
    
    
	/**
	 * 
	 * 情報生成
	 * 
	 * @param SaitenCondition 
	 * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
	 */
    private List makeListMiSaitenKyoin(MisaitenJugyoCondition condition) 
    	throws NoSuchDataException {
	    // DAOの取得
	    final UPDataAccessObject dao = getUPDataAccessObject();
	
	    // SQLを取得
	    SQLHelper sqlHelperTeiki = null;
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu Start
//	    SQLHelper sqlHelperTsuiSai = null;
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu End
	    SQLHelper sqlHelperKyoinSu = null;
	    SQLHelper sqlHelperJugyoSu = null;
	
	    // データ変換オブジェクト(VE)を生成
	    final SaitenValueExchanger ve
	        = new SaitenValueExchanger();
	    
		// 検索条件
		List listCondition = new ArrayList();
		List kyoinSuListCondition = new ArrayList();
		List listTeiki;
	    List listTsuiSai;
	    List listJugyo;

//		 UPEX-1385 2010/06/11 k.higashida Start
	    // パラメータテーブルより留学・休学を除外するかどうかの情報を取得する
		String para = "";
		String kyugakKbn = "0";
		String ryugakKbn = "0";
        CouParamDAO couParaDao = (CouParamDAO) super.getDbSession().getDao(
                CouParamDAO.class);
        CouParamAR couParamAR;
        try {
        	couParamAR = couParaDao.findByPrimaryKey("KMC", "KYUGAK_RYUGAK_JOGAI", 0);
       		para = UtilStr.cnvNull(couParamAR.getValue());
       		
       		// 取得したパラメーターの区分によって休学・留学の検索条件を設定する
       		if (para.equals("0")) {
       			// 休学、留学を除外しない
       			kyugakKbn = "0";
       			ryugakKbn = "0";
       		} else if (para.equals("1")) {
       			// 休学のみ除外する
       			kyugakKbn = "1";
       			ryugakKbn = "0";
       		} else if (para.equals("2")) {
       			// 留学のみ除外する
       			kyugakKbn = "0";
       			ryugakKbn = "1";
       		} else if (para.equals("3")) {
       			// 休学、留学を除外する
       			kyugakKbn = "1";
       			ryugakKbn = "1";
       		} else {
       			// 休学、留学を除外する(0,1,2,3以外の場合)
       			kyugakKbn = "1";
       			ryugakKbn = "1";
       		}
        } catch (DbException e) {
            throw new GakuenSystemException(e);
        } catch (NullPointerException e) {
            throw new GakuenSystemException("分類:KMC、項目:KYUGAK_RYUGAK_JOGAIのパラメータが存在しません",
                    e);
        } catch (Exception e) {
            throw new GakuenSystemException(e);
        }
//		 UPEX-1385 2010/06/11 k.higashida End
	    
	    // 検索条件によりSQLを呼び分ける
	    if ("".equals(condition.getKanriBusyoCode())) {
	    	// 全て対象の場合
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu Start
//	    	sqlHelperTeiki =  new SQLHelper("KM00050");
//	    	sqlHelperTsuiSai =  new SQLHelper("KM00051");
	        sqlHelperTeiki =  new SQLHelper("KM00169");
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu End
	    	sqlHelperKyoinSu =  new SQLHelper("KM00056");
	    	sqlHelperJugyoSu =  new SQLHelper("KM00140");

//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu Start
//			listTeiki = ve.preparedTeikiBindingValues(condition);
//	    	 UPEX-1385 2010/06/11 k.higashida Start
//			listTeiki = ve.preparedTeikiTsuiSaiBindingValues(condition);
			listTeiki = ve.preparedTeikiTsuiSaiBindingValues(condition, kyugakKbn, ryugakKbn);
//	    	 UPEX-1385 2010/06/11 k.higashida End
//			listTsuiSai = ve.preparedTsuiSaiBindingValues(condition);
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu End
//	    	 UPEX-1385 2010/06/11 k.higashida Start
//			listJugyo = ve.preparedJugyoValues(condition);
			listJugyo = ve.preparedJugyoValues(condition, kyugakKbn, ryugakKbn);
//	    	 UPEX-1385 2010/06/11 k.higashida End
//UPEX-742 授業数分母の件数にあわせて教員数も採点運用期間を見る 2008.10.08 Horiguchi Start
//			kyoinSuListCondition.add(condition.getNendo());
//			kyoinSuListCondition.add(condition.getGakkiNo());
//			kyoinSuListCondition.add(condition.getNendo());
//			kyoinSuListCondition.add(condition.getGakkiNo());
	        Timestamp sysDate = new Timestamp(DateFactory.getInstance().getTime());
	        kyoinSuListCondition.add(condition.getNendo());
			kyoinSuListCondition.add(condition.getGakkiNo());
			kyoinSuListCondition.add(sysDate);
			kyoinSuListCondition.add(sysDate);
//			 UPEX-1385 2010/06/11 k.higashida Start
			kyoinSuListCondition.add(kyugakKbn);
			kyoinSuListCondition.add(kyugakKbn);
			kyoinSuListCondition.add(ryugakKbn);
			kyoinSuListCondition.add(ryugakKbn);
//			 UPEX-1385 2010/06/11 k.higashida End
			kyoinSuListCondition.add(condition.getNendo());
			kyoinSuListCondition.add(condition.getGakkiNo());
			kyoinSuListCondition.add(condition.getNendo());
			kyoinSuListCondition.add(condition.getGakkiNo());
			kyoinSuListCondition.add(sysDate);
			kyoinSuListCondition.add(sysDate);
//			 UPEX-1385 2010/06/11 k.higashida Start
			kyoinSuListCondition.add(kyugakKbn);
			kyoinSuListCondition.add(kyugakKbn);
			kyoinSuListCondition.add(ryugakKbn);
			kyoinSuListCondition.add(ryugakKbn);
//			 UPEX-1385 2010/06/11 k.higashida End
//UPEX-742 授業数分母の件数にあわせて教員数も採点運用期間を見る 2008.10.08 Horiguchi End
	    } else {
	    	// 管理部署の指定ありの場合
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu Start
//	    	sqlHelperTeiki =  new SQLHelper("KM00052");
//	    	sqlHelperTsuiSai =  new SQLHelper("KM00053");
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu End
	        sqlHelperTeiki =  new SQLHelper("KM00170");
	    	sqlHelperKyoinSu =  new SQLHelper("KM00057");
	    	sqlHelperJugyoSu =  new SQLHelper("KM00141");
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu Start	    	
//			listTeiki = ve.preparedTeikiKanriBusyoBindingValues(condition);
//	    	 UPEX-1385 2010/06/11 k.higashida Start
//	    	listTeiki = ve.preparedTeikiTsuiSaiKanriBusyoBindingValues(condition);
	    	listTeiki = ve.preparedTeikiTsuiSaiKanriBusyoBindingValues(condition, kyugakKbn, ryugakKbn);
//	    	 UPEX-1385 2010/06/11 k.higashida End
//			listTsuiSai = ve.preparedTsuiSaiKanriBusyoBindingValues(condition);
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu End
//	    	 UPEX-1385 2010/06/11 k.higashida Start
//			listJugyo = ve.preparedJugyoForKanriBushoValues(condition);
			listJugyo = ve.preparedJugyoForKanriBushoValues(condition, kyugakKbn, ryugakKbn);
//	    	 UPEX-1385 2010/06/11 k.higashida End
//UPEX-742 授業数分母の件数にあわせて教員数も採点運用期間を見る 2008.10.08 Horiguchi Start
//			kyoinSuListCondition.add(condition.getNendo());
//			kyoinSuListCondition.add(condition.getGakkiNo());
//			kyoinSuListCondition.add(condition.getKanriBusyoCode());
//			kyoinSuListCondition.add(condition.getNendo());
//			kyoinSuListCondition.add(condition.getGakkiNo());
//			kyoinSuListCondition.add(condition.getKanriBusyoCode());
			Timestamp sysDate = new Timestamp(DateFactory.getInstance().getTime());
			kyoinSuListCondition.add(condition.getNendo());
			kyoinSuListCondition.add(condition.getGakkiNo());
			kyoinSuListCondition.add(condition.getKanriBusyoCode());
			kyoinSuListCondition.add(sysDate);
			kyoinSuListCondition.add(sysDate);
//			 UPEX-1385 2010/06/11 k.higashida Start
			kyoinSuListCondition.add(kyugakKbn);
			kyoinSuListCondition.add(kyugakKbn);
			kyoinSuListCondition.add(ryugakKbn);
			kyoinSuListCondition.add(ryugakKbn);
//			 UPEX-1385 2010/06/11 k.higashida End
			kyoinSuListCondition.add(condition.getNendo());
			kyoinSuListCondition.add(condition.getGakkiNo());
			kyoinSuListCondition.add(condition.getNendo());
			kyoinSuListCondition.add(condition.getGakkiNo());
			kyoinSuListCondition.add(condition.getKanriBusyoCode());
			kyoinSuListCondition.add(sysDate);
			kyoinSuListCondition.add(sysDate);
//			 UPEX-1385 2010/06/11 k.higashida Start
			kyoinSuListCondition.add(kyugakKbn);
			kyoinSuListCondition.add(kyugakKbn);
			kyoinSuListCondition.add(ryugakKbn);
			kyoinSuListCondition.add(ryugakKbn);
//			 UPEX-1385 2010/06/11 k.higashida End
//UPEX-742 授業数分母の件数にあわせて教員数も採点運用期間を見る 2008.10.08 Horiguchi End
	    }

	    UtilLog.debug(this.getClass(), 
	    		"\n--- sqlHelperTeiki = [" + sqlHelperTeiki + "]");
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu Start
//	    UtilLog.debug(this.getClass(), 
//	    		"\n--- sqlHelperTsuiSai = [" + sqlHelperTsuiSai + "]");
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu End
	    UtilLog.debug(this.getClass(), 
	    		"\n--- sqlHelperKyoinSu = [" + sqlHelperKyoinSu + "]");
	    UtilLog.debug(this.getClass(), 
	    		"\n--- sqlHelperJugyoSu = [" + sqlHelperJugyoSu + "]");

		// SQLを設定
		dao.setSQL(sqlHelperKyoinSu);

		// 教員数
		int kyoinSu = 0;
		try {
			kyoinSu =
			   	ve.getKyoinSu(getUPDataAccessObject().
			   					find(kyoinSuListCondition));
		} finally {
        	//2007-01-17 クローズ漏れ対応
        	dao.clearSQL();
        }
		
		// SQLを設定
		dao.setSQL(sqlHelperJugyoSu);

		// 授業数
		int jugyoSu = 0;
		try {
			jugyoSu = 
				ve.getJugyoSu(getUPDataAccessObject().find(listJugyo));
		} finally {
        	//2007-01-17 クローズ漏れ対応
        	dao.clearSQL();
        }

		// SQLを設定
		dao.setSQL(sqlHelperTeiki);

		// 定期試験の未採点教員一覧
		Map misaitenTeikiMap = null;

		try {		
			misaitenTeikiMap =
				ve.mapMiSaitenKyoin(getUPDataAccessObject().find(listTeiki),
						misaitenTeikiMap);
		} catch (NoSuchDataException e) {
			// 該当データがない場合あり。
			misaitenTeikiMap = new HashMap();
		} finally {
        	//2007-01-17 クローズ漏れ対応
        	dao.clearSQL();
        }
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu Start
//		dao.setSQL(sqlHelperTsuiSai);

		// 追試試験の未採点教員一覧
//		try {		
//			misaitenTeikiMap = 
//				ve.mapMiSaitenKyoin(getUPDataAccessObject().find(listTsuiSai),
//						misaitenTeikiMap);
//		} catch (NoSuchDataException e) {
//			// 該当データがない場合あり。
//		} finally {
//       	//2007-01-17 クローズ漏れ対応
//        	dao.clearSQL();
//      }
//UPEX-410 定期試験と追再試験の件数を集計する 2008.1.11 K.Tamotsu End
		if (misaitenTeikiMap.isEmpty()) {
    		throw new 
			NoSuchDataException("指定したデータが見つかりませんでした。");
		}

		List list = ve.listMiSaitenKyoin(
				getDbSession(), misaitenTeikiMap, kyoinSu, jugyoSu);
		
    	return list;
    }
    
    /**
     *  事後条件チェック
     * 
     * @param list 取得リスト
     * 
     */
    private void selfTestListMiSaitenKyoinPost(List list) 
    	throws NoSuchDataException {
    	
    	if (list == null) {
    		throw new 
				NoSuchDataException("指定したデータが見つかりませんでした。");
    	}
    	
    	for (int i = 0;i < list.size(); i++) {
    	    MisaitenJugyoDTO dto = (MisaitenJugyoDTO)list.get(i);
    		
    		if (dto == null) {
    			throw new BusinessRuleException("未採点授業がnull エラー");
    		}
    		if (dto.getKyoinMei() == null) {
    			throw new BusinessRuleException("教員名がnull エラー");
    		}
    		
    	}
    }
    
    /**
     *  事後条件チェック
     * 
     * @param list 取得リスト
     * 
     */
    private void selfTestListSaitenUnyouPost(List list) 
    	throws NoSuchDataException {
    	
    	if (list == null) {
    		throw new 
				NoSuchDataException("指定したデータが見つかりませんでした。");
    	}
    	
    	for (int i = 0;i < list.size(); i++) {
    		SaitenUnyoListDTO dto = (SaitenUnyoListDTO)list.get(i);
    		
    		if (dto.getKanriBsyoCd() == null) {
    			throw new BusinessRuleException("管理部署コードがnull エラー");
    		}
    		
    		if (dto.getShikenKbn() == null) {
    			throw new BusinessRuleException("試験区分がnull エラー");
    		}

//    		 V1.2対応 2009/10/14 k.higashida Start
    		if (dto.getShikenKaisu() == null) {
    			throw new BusinessRuleException("試験回数がnull エラー");
    		}
//    		 V1.2対応 2009/10/14 k.higashida End
    		
    		if (dto.getSaitenKbn() == null) {
    			throw new BusinessRuleException("採点方法区分がnull エラー");
    		}
    		
    		if (dto.getSaitenKaishibi() == null) {
    			throw new BusinessRuleException("採点開始日がnull エラー");
    		}

    		if (dto.getSaitenShuryobi() == null) {
    			throw new BusinessRuleException("採点終了日がnull エラー");
    		}   		
    	}
    }
    
    /**
     * 
     * 指定した採点運用方法を取得する
     * 
     * @param condition 検索条件
     * @return rgstStnUyhh 採点運用方法DTO
     * @throws NoSuchDataException
     */
    public SaitenUnyoHohoDTO acquireSaitenUnyoHoho(SaitenUnyoCondition condition)
    	throws NoSuchDataException {

        // 事前条件チェック
        selfTestAcquireSaitenUnyoHohoPre(condition);
        
        // 情報生成
        SaitenUnyoHohoDTO rgstStnUyhh = acquireSaitenUnyoHohoMaker(condition);
		
		// 事後条件チェック
        selfTestAcquireSaitenUnyoHohoPost(rgstStnUyhh);
        
        return rgstStnUyhh;
    }

    /**
     * 事後条件チェック
     * 
	 * @param rgstStnUyhh 採点運用方法
	 */
	private void selfTestAcquireSaitenUnyoHohoPost(
	        SaitenUnyoHohoDTO idoSaitenUnyoDTO) {

    	if (idoSaitenUnyoDTO == null) {
    		throw new BusinessRuleException("指定した採点運用方法登録条件が null");
    	}		
    	
		if (idoSaitenUnyoDTO.getKanriBusyoCd() == null) {
			throw new BusinessRuleException("管理部署コードが null");
		}
	
		if (idoSaitenUnyoDTO.getShikenKbn() == null) {
			throw new BusinessRuleException("試験区分が null");
		}
	
		if (idoSaitenUnyoDTO.getSaitenHohoKbn() == null) {
			throw new BusinessRuleException("採点方法区分が null");
		}
		
		if (idoSaitenUnyoDTO.getTorokuKaishibi() == null) {
			throw new BusinessRuleException("採点登録開始日時が null");
		}
	
		if (idoSaitenUnyoDTO.getTorokuShuryobi() == null) {
			throw new BusinessRuleException("採点登録終了日時が null");
		}  

		if (idoSaitenUnyoDTO.getIdoKbn() == null) {
			throw new BusinessRuleException("異動区分リストが null");
		}
	}

	/**
	 * 情報生成
	 * 
	 * @param condition
	 * @return SaitenUnyoHohoDTO
	 * @throws DbException
	 * @throws NoSuchDataException
	 */
	private SaitenUnyoHohoDTO acquireSaitenUnyoHohoMaker(
			SaitenUnyoCondition condition) throws NoSuchDataException {

		// SQLを設定 採点運用方法一覧情報(異動区分以外)取得
		final SQLHelper sqlHelper =
							new SQLHelper(ISQLContents.ID_KM + "00110");
		// SQLを設定 採点運用方法一覧情報取得
		getUPDataAccessObject().setSQL(sqlHelper);

		// データ変換オブジェクトを生成
		// 採点運用方法一覧情報取得
		final SaitenUnyoExchanger ve = new SaitenUnyoExchanger();
		SaitenUnyoHohoDTO idoSaitenUnyoDTO = null;
		try {
			idoSaitenUnyoDTO = 
	        	ve.acquireSaitenUnyoHoho(getUPDataAccessObject().find(
	            ve.preparedStatementBindingValues(condition)));
		} finally {
        	//2007-01-17 クローズ漏れ対応
			getUPDataAccessObject().clearSQL();
        }
		
		// SQLを設定 採点運用方法一覧情報(異動区分)取得
		final SQLHelper sqlHelperIdo =
							new SQLHelper(ISQLContents.ID_KM + "00111");
		// SQLを設定 採点運用方法一覧情報取得
		getUPDataAccessObject().setSQL(sqlHelperIdo);
		// データ変換オブジェクトを生成
		// 採点運用方法一覧情報取得
		final SaitenUnyoExchanger veIdo = new SaitenUnyoExchanger();

		SaitenUnyoHohoDTO saitenUnyoDTO = new SaitenUnyoHohoDTO();

		try {
			saitenUnyoDTO = 
	        	veIdo.acquireSaitenUnyoHoho2(getUPDataAccessObject().find(
	            veIdo.preparedStatementBindingValues(condition)) ,idoSaitenUnyoDTO);
        } catch (NoSuchDataException e) {
            List dtoList = new ArrayList();
            idoSaitenUnyoDTO.setIdoKbn(new ArrayList());
            saitenUnyoDTO = idoSaitenUnyoDTO;
        } finally {
        	//2007-01-17 クローズ漏れ対応
        	getUPDataAccessObject().clearSQL();
        }
	    return saitenUnyoDTO ;
	}

	/**
	 * 事前条件チェック
	 * 
	 * @param condition
	 */
	private void selfTestAcquireSaitenUnyoHohoPre(SaitenUnyoCondition condition) {

        if (condition == null) {
            throw new BusinessRuleException("検索条件に null は設定できません。");
        }
        
        if (condition.getKanriBsyoCd() == null) {
            throw new BusinessRuleException("管理部署コードに null は設定できません。");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("開講年度に null は設定できません。");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("学期NOに null は設定できません。");
        }

        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException("試験区分に null は設定できません。");
        }
	}

    /**
     * 
     * 指定した採点運用方法を登録する
     * 
     * @param condition 更新条件
     * @throws AlreadyUpdatePossibilityException 既に更新されている可能性がある場合の例外です。
     * @throws AlreadyUpdateException 既にデータが更新されている場合
     */
    public void registSaitenUnyoHoho(SaitenUnyoHohoDTO saitenUnyoHohoDTO)
    	throws 	AlreadyUpdateException, 
    	AlreadyUpdatePossibilityException {
        // 事前条件チェック
        selfTestRegistSaitenUnyoHohoPre(saitenUnyoHohoDTO);
		// 情報生成
		insertRegistSaitenUnyoHoho(saitenUnyoHohoDTO);
    }
   
	/**
	 * 事前条件チェック
	 * 
	 * @param condition 更新条件
	 */
	private void selfTestRegistSaitenUnyoHohoPre(SaitenUnyoHohoDTO condition) {

        if (condition == null) {
            throw new BusinessRuleException(
                    "更新条件に null は設定できません。");
        }
        
        if (condition.getKanriBusyoCd() == null) {
            throw new BusinessRuleException(
                    "管理部署コードに null は設定できません。");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException(
                    "開講年度に null は設定できません。");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException(
                    "学期NOに null は設定できません。");
        }

        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException(
                    "試験区分に null は設定できません。");
        }

//      V1.2対応 2009/10/14 k.higashida Start
        if (condition.getShikenKaisu() == null) {
            throw new BusinessRuleException(
                    "試験回数に null は設定できません。");
        }
//      V1.2対応 2009/10/14 k.higashida End
        
        if (condition.getTorokuKaishibi() == null) {
            throw new BusinessRuleException(
                    "採点登録開始日に null は設定できません。");
        }

        if (condition.getTorokuShuryobi() == null) {
            throw new BusinessRuleException(
                    "採点登録終了日に null は設定できません。");
        }

        if (condition.getSaitenHohoKbn() == null) {
            throw new BusinessRuleException(
                    "採点方法区分に null は設定できません。");
        }

        if (condition.getIdoKbn() == null) {
            throw new BusinessRuleException(
                    "異動区分に null は設定できません。");
        }
	}
	
	/**
	 * DB登録処理
	 * 
     * @param condition 更新条件
     * @throws AlreadyUpdatePossibilityException 既に更新されている可能性がある場合の例外です。
     * @throws AlreadyUpdateException 既にデータが更新されている場合
	 */
	private void insertRegistSaitenUnyoHoho(
		SaitenUnyoHohoDTO condition) throws 
		AlreadyUpdateException, 
		AlreadyUpdatePossibilityException {
		try {

			// 採点運用DAO生成
			KmcStnUnyoDAO stnUnyoDAO = (KmcStnUnyoDAO)
				getDbSession().getDao(KmcStnUnyoDAO.class);
			KmcStnUnyoAR stnUnyoAR = stnUnyoDAO.findByPrimaryKey(
				condition.getKanriBusyoCd(),
				condition.getKaikoNendo().intValue(),
				condition.getGakkiNo().intValue(),
//			 V1.2対応 2009/10/14 k.higashida Start
//				condition.getShikenKbn().toString());
				condition.getShikenKbn().toString(),
				condition.getShikenKaisu().intValue()
				);
//			 V1.2対応 2009/10/14 k.higashida End
						
			// 新規
			if (stnUnyoAR == null) {
				// 登録情報設定
				stnUnyoAR = new KmcStnUnyoAR(
						getDbSession(),
						condition.getKanriBusyoCd(),
						condition.getKaikoNendo().intValue(),
						condition.getGakkiNo().intValue(),
//				 V1.2対応 2009/10/14 k.higashida Start
//						condition.getShikenKbn());
						condition.getShikenKbn(),
						condition.getShikenKaisu().intValue());
//				 V1.2対応 2009/10/14 k.higashida End
				
				stnUnyoAR.setTorokuKaishibi(condition.getTorokuKaishibi());
				// Timestampの扱いが保留
				// condition.getSaitenEndTimestamp());
				stnUnyoAR.setTorokuShuryobi(condition.getTorokuShuryobi());
				// Timestampの扱いが保留
				// condition.getSaitenEndTimestamp());
				stnUnyoAR.setSaitenKbn(condition.getSaitenHohoKbn());
				// 採点運用TBLに登録
				stnUnyoAR.store();
/*
 2006.07.18 異動者採点除外区分の設定を実施しないように変更
				// 異動者採点除外区分TBL更新
				// INSERT
				// 異動者採点除外区分DAO生成
				KmcStnIdoshaDAO kmcStnIdoshaDAO = (KmcStnIdoshaDAO) 
					getDbSession().getDao(KmcStnIdoshaDAO.class);
				// 異動区分毎
		        List idoDtoList = (List)condition.getIdoKbn();
		        for (int i = 0; i < idoDtoList.size(); i++){
		            String idokbn = (String)idoDtoList.get(i);

					KmcStnIdoshaAR kmcStnIdoshaAR = 
					    kmcStnIdoshaDAO.findByPrimaryKey(
					condition.getKanriBusyoCd(),
					condition.getKaikoNendo().intValue(),
					condition.getGakkiNo().intValue(),
					condition.getShikenKbn(),
					idokbn);
		            // 登録済みだった場合、エラー
					if (kmcStnIdoshaAR != null) {
			            throw new BusinessRuleException(
			                "kmc_Stn_Idoshaテーブルに該当データが存在します。");
					}
					KmcStnIdoshaAR kmcStnIdoshaARIns = new KmcStnIdoshaAR(
							getDbSession(),
							condition.getKanriBusyoCd(),
							condition.getKaikoNendo().intValue(),
							condition.getGakkiNo().intValue(),
							condition.getShikenKbn(),
							idokbn);

					kmcStnIdoshaARIns.store();
		        }
*/
				
			// 更新
			} else {

				stnUnyoAR.setTorokuKaishibi(condition.getTorokuKaishibi());
				// Timestampの扱いが保留
				// condition.getSaitenEndTimestamp());
				stnUnyoAR.setTorokuShuryobi(condition.getTorokuShuryobi());
				// Timestampの扱いが保留
				// condition.getSaitenEndTimestamp());
				stnUnyoAR.setSaitenKbn(condition.getSaitenHohoKbn());
				stnUnyoAR.store();
/*	
 2006.07.18 異動者採点除外区分の設定を実施しないように変更			
				// 異動者採点除外区分TBL更新
				// DELETE & INSERT

				// DELETE
   			    // DAOの取得
   			    final UPDataAccessObject dao = getUPDataAccessObject();
			    SQLHelper delSqlHelperIdo = 
			        new SQLHelper(ISQLContents.ID_KM + "00112");
			    UtilLog.debug(this.getClass(),
			    			"\n--- sqlHelper = [" + delSqlHelperIdo + "]");
				dao.setSQL(delSqlHelperIdo);

				// 削除キー設定
				List listConditionDel = new ArrayList();
				listConditionDel.add(condition.getKanriBusyoCd());
				listConditionDel.add(condition.getKaikoNendo());
				listConditionDel.add(condition.getGakkiNo());
				listConditionDel.add(condition.getShikenKbn());
								
			    try{
					// 削除実行
					getUPDataAccessObject().remove(listConditionDel);
				} catch (NoSuchDataException e) {
				    // データ０件も有り得る為、削除対象レコードが無い場合は
				    // スルーする
				}

		        // INSERT				
				// 異動者採点除外区分DAO生成
				KmcStnIdoshaDAO kmcStnIdoshaDAO = (KmcStnIdoshaDAO)
					getDbSession().getDao(KmcStnIdoshaDAO.class);
				KmcStnIdoshaAR kmcStnIdoshaAR = null;
				// 異動区分毎
		        List idoDtoList = (List)condition.getIdoKbn();
		        for (int i = 0; i < idoDtoList.size(); i++){
		            String idokbn = (String)idoDtoList.get(i);

					// 登録情報設定
				    kmcStnIdoshaAR = new KmcStnIdoshaAR(
							getDbSession(),
							condition.getKanriBusyoCd(),
							condition.getKaikoNendo().intValue(),
							condition.getGakkiNo().intValue(),
							condition.getShikenKbn(),
							idokbn);
					kmcStnIdoshaAR.store();
		        }
*/		        		        
			}
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		} catch (GakuenException e) {
			throw new GakuenSystemException(e);
        }

	}

    /**
     * 
     * 指定した採点運用方法を削除する
     * 
     * @param condition 採点運用検索条件
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * @throws AlreadyUpdatePossibilityException 既に更新されている可能性がある場合の例外
     * @throws AlreadyUpdateException 既にデータが更新されている場合の例外
     */
    public void delete(SaitenUnyoCondition condition)
    	throws NoSuchDataException,
    	AlreadyUpdateException, 
    	AlreadyUpdatePossibilityException {

        // 事前条件チェック
        selfTestDeletePre(condition);
		// 削除処理実施
		deleteSaitenUnyoHoho(condition);
    }
    
	/**
	 * 事前条件チェック
	 * 
	 * @param condition 採点運用検索条件
	 */
	private void selfTestDeletePre(SaitenUnyoCondition condition) {

        if (condition == null) {
            throw new BusinessRuleException
            	("検索条件に null は設定できません。");
        }
        
        if (condition.getKanriBsyoCd() == null) {
            throw new BusinessRuleException
            	("管理部署コードに null は設定できません。");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException
            	("開講年度に null は設定できません。");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException
            	("学期NOに null は設定できません。");
        }

        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException
            	("試験区分に null は設定できません。");
        }
//      V1.2対応 2009/10/14 k.higashida Start
        if (condition.getShikenKaisu() == null) {
            throw new BusinessRuleException
            	("試験回数に null は設定できません。");
        }
//      V1.2対応 2009/10/14 k.higashida End
        
	}
	
	/**
	 * DB削除処理
	 * 
     * @param condition 採点運用検索条件
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * @throws AlreadyUpdatePossibilityException 既に更新されている可能性がある場合の例外
     * @throws AlreadyUpdateException 既にデータが更新されている場合の例外
	 */
	private void deleteSaitenUnyoHoho(SaitenUnyoCondition condition) 
		throws 	NoSuchDataException, 
				AlreadyUpdateException, 
				AlreadyUpdatePossibilityException {
	    try {

	        // 採点運用テーブル
	        // 採点運用DAO生成
			KmcStnUnyoDAO stnUnyoDAO = (KmcStnUnyoDAO) 
				getDbSession().getDao(KmcStnUnyoDAO.class);
			KmcStnUnyoAR stnUnyoAR = stnUnyoDAO.findByPrimaryKey(
				condition.getKanriBsyoCd(),
				condition.getKaikoNendo().intValue(),
				condition.getGakkiNo().intValue(),
//				 V1.2対応 2009/10/14 k.higashida Start
//				condition.getShikenKbn());
				condition.getShikenKbn(),
				condition.getShikenKaisu().intValue()
				);
//				 V1.2対応 2009/10/14 k.higashida End

			if (stnUnyoAR == null) {
				throw new NoSuchDataException
					("指定したデータが見つかりません。");
			}
			// 採点運用TBLからデータ削除
			stnUnyoAR.remove();

/*
 2006.07.18 異動者採点除外区分の設定を実施しないように変更
	        // 異動者採点除外区分テーブル
		    // DAOの取得
		    final UPDataAccessObject dao = getUPDataAccessObject();
		    SQLHelper delSqlHelperIdo = 
		        new SQLHelper(ISQLContents.ID_KM + "00112");
		    UtilLog.debug(this.getClass(),
		    			"\n--- sqlHelper = [" + delSqlHelperIdo + "]");
			dao.setSQL(delSqlHelperIdo);

			// 削除キー設定
			List listConditionDel = new ArrayList();
			listConditionDel.add(condition.getKanriBsyoCd());
			listConditionDel.add(condition.getKaikoNendo());
			listConditionDel.add(condition.getGakkiNo());
			listConditionDel.add(condition.getShikenKbn());

		    try{
				// 削除実行
				getUPDataAccessObject().remove(listConditionDel);
			} catch (NoSuchDataException e) {
			    // データ０件も有り得る為、削除対象レコードが無い場合は
			    // スルーする
			}
*/
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		} catch (GakuenException e) {
			throw new GakuenSystemException(e);
		}			
	}


    /**
     * 
     * 評価基準を取得する
     * 
     * @param condition 試験別評価基準検索条件
     * @return hyokaKijunList<HyokaKijun> 評価基準リスト
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    public List listShikenBetuHyokaKijun(ShikenBetuHyokaKijunCondition condition)
    	throws NoSuchDataException {
        // 事前条件チェック
        selfTestListShikenBetuHyokaKijunPre(condition);
        // 情報生成
        List hyokaKijunList = listShikenBetuHyokaKijunMaker(condition);
        
        return hyokaKijunList;
    }

	/**
	 * 
	 * 事前条件チェック
	 * 
	 * @param condition 試験別評価基準検索条件
	 * 
	 */
	private void selfTestListShikenBetuHyokaKijunPre(
	    ShikenBetuHyokaKijunCondition condition) {

        if (condition == null) {
            throw new BusinessRuleException
            	("評価基準検索条件がNULLでない");
        }
        
        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException
            	("評価基準検索条件の管理番号がNULLでない");
        }

        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException
            	("評価基準検索条件の試験区分がNULLでない");
        }
        
	}


    /**
     * 
     * 情報生成
     * 
	 * @param condition 試験別評価基準検索条件
     * @return hyokaKijunList<HyokaKijun> 評価基準リスト
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    private List listShikenBetuHyokaKijunMaker(
            ShikenBetuHyokaKijunCondition condition)
    	throws NoSuchDataException {
        
        List list = new ArrayList();

        if (condition.getShikenKbn().equals(String.valueOf(ShikenUPKbn.TEIKISHIKEN.getCode()))) {
            list = listHyokaKijun(String.valueOf(condition.getKanriNo()));
        } else if (condition.getShikenKbn().equals(String.valueOf(ShikenUPKbn.TSUISHIKEN.getCode()))) {
            list = listHyokaKijun(String.valueOf(condition.getKanriNo()));
            List clList = new ArrayList();
            clList.addAll(list);
            boolean sikenKbnFlg = false;
    		for (int i = 0; i < list.size(); i++ ) {
    		    HyokaKijunDTO dto = (HyokaKijunDTO)list.get(i);
    		    if (dto.getTuisiHyokaMaxFlg().intValue() == 0){
    		        list.remove(i);
    		        i--;
    		    } else if (dto.getTuisiHyokaMaxFlg().intValue() == 1 ){
    		        sikenKbnFlg = true;
    		        break;
    		    }
    		}
    		// １が存在しなかった場合。
    		if (!sikenKbnFlg) {
    		    list = clList;
    		}
        
        } else if (condition.getShikenKbn().equals(String.valueOf(ShikenUPKbn.SAISHIKEN.getCode()))) {
            list = listHyokaKijun(String.valueOf(condition.getKanriNo()));
            List clList = new ArrayList();
            clList.addAll(list);
            boolean sikenKbnFlg = false;
    		for (int i = 0; i < list.size(); i++ ) {
    		    HyokaKijunDTO dto = (HyokaKijunDTO)list.get(i);
    		    if (dto.getSaisiHyokaMaxFlg().intValue() == 0) {
    		        list.remove(i);
    		        i--;
    		    } else if (dto.getSaisiHyokaMaxFlg().intValue() == 1) {
    		        sikenKbnFlg = true;
    		        break;
    		    }
    		}
    		// １が存在しなかった場合。
    		if (!sikenKbnFlg) {
    		    list = clList;
    		}
        }    	
    	return list;
    }
    
    /**
     * 指定された条件で除外対象の異動区分を一覧にします。
     * 
     * @param condition 検索条件
     * @return 異動区分の一覧
     */
    public List listJogaiTaishoIdoKubun(
    		JogaiTaishoIdoKubunCondition condition) {
    	testListJogaiTaishoIdoKubunPre(condition);
    	
    	getUPDataAccessObject().setSQL(new SQLHelper(
    			ISQLContents.ID_KM + "00134"));
    	
    	final IdoKubunValueExchanger ve = new IdoKubunValueExchanger();
    	
		try {
			final List list = ve.listJogaiTaisho(getUPDataAccessObject().
	    			find(ve.preparedStatementBindingValuesForJogaiTaisho(
	    					condition)));
			
	    	testListJogaiTaishoIdoKubunPost(list);
	    	
			return list;
		} catch (NoSuchDataException e) {
			return new ArrayList(0);
		} finally {
			getUPDataAccessObject().clearSQL();
		}
    }

	private void testListJogaiTaishoIdoKubunPre(JogaiTaishoIdoKubunCondition condition) {
		if (condition == null) {
			throw new BusinessRuleException("検索条件に null は設定できません。");
		}
		if (condition.getJugyoCode() == null) {
			throw new BusinessRuleException("検索条件の授業コードに null は設定できません。");
		}
		if (condition.getShikenKubun() == null) {
			throw new BusinessRuleException("検索条件の試験区分に null は設定できません。");
		}
	}

	private void testListJogaiTaishoIdoKubunPost(List list) {
		if (list == null) {
			throw new BusinessRuleException("検索結果の一覧に null は設定できません。");
		}
		
		final Iterator ite = list.iterator();
		while (ite.hasNext()) {
			final Object obj = ite.next();
			if (obj == null) {
				throw new BusinessRuleException("一覧の内容に null は設定できません。");
			}
			if (obj instanceof String == false) {
				throw new BusinessRuleException("一覧の内容に文字列以外は設定できません。");
			}
		}
	}

	
	
	
    /**
     * 
     * 追再試験のMAXフラグに紐づく素点の最大値を取得する
     * 
     * @param condition 試験別評価基準検索条件
     * @return String 素点TO
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    public int acquireShikenBetuSotenMax(ShikenBetuHyokaKijunCondition condition)
    	throws NoSuchDataException {
        // 事前条件チェック
        selfTestAcquireShikenBetuSotenMaxPre(condition);
        // 情報生成
        int sotenMax = getAcquireShikenBetuSotenMax(condition);
        
        return sotenMax;
    }
	
	/**
	 * 
	 * 事前条件チェック
	 * 
	 * @param condition 試験別評価基準検索条件
	 * 
	 */
	private void selfTestAcquireShikenBetuSotenMaxPre(
	    ShikenBetuHyokaKijunCondition condition) {

        if (condition == null) {
            throw new BusinessRuleException
            	("評価基準検索条件がNULLでない");
        }
        
        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException
            	("評価基準検索条件の管理番号がNULLでない");
        }

        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException
            	("評価基準検索条件の試験区分がNULLでない");
        }

        if (!(condition.getShikenKbn().equals(String.valueOf(ShikenUPKbn.TSUISHIKEN.getCode())) || 
                condition.getShikenKbn().equals(String.valueOf(ShikenUPKbn.SAISHIKEN.getCode())))) {
               throw new BusinessRuleException
               	("評価基準検索条件の試験区分が1と2以外が入力されています");
           }


	}

   /**
     * 
     * 情報生成
     * 
	 * @param condition 試験別評価基準検索条件
     * @return String 素点To
     * @throws NoSuchDataException 指定したデータが見つからなかった場合の例外
     * 
     */
    private int getAcquireShikenBetuSotenMax(
            ShikenBetuHyokaKijunCondition condition)
    	throws NoSuchDataException {
        
        int sotenTo = 0;
    	
    	// DAOの取得
    	final UPDataAccessObject dao = getUPDataAccessObject();
    	
    	// 動的SQLの生成
		final AbstractDynamicSQLContentsFactory factory =
			AbstractDynamicSQLContentsFactory.getFactory(
					AbstractDynamicSQLContentsFactory.JG00043);

		final SQLHelper sqlHelper =
			new SQLHelper(factory.create(condition));

    	// データ変換オブジェクト(VE)を生成
    	final SaitenValueExchanger ve = new SaitenValueExchanger();

    	// SQLを設定
    	dao.setSQL(sqlHelper);

	    try { 
	    	// VEに検索条件をセット
	    	// DBから必要な情報を取得する。
	    	sotenTo = 
	    	    ve.acquireShikenBetuSotenMax(
	    	    		getDbSession() ,getUPDataAccessObject().find(
	    	    				ve.preparedStatementBindingValues(
	    	    				        condition)));
	    } finally {
	    	//2007-01-17 クローズ漏れ対応
	    	dao.clearSQL();
	    }
	    
        return sotenTo;
    }

    
    /**
     * 既に登録されている採点登録情報から削除対象となるデータを抽出します。(定期試験)
     * @param con
     * @return list
     */
    public List listSaitenIkkatsuDeleteTargetTeiki(
    		SaitenIkkatsuDeleteCondition con) throws NoSuchDataException {
    	
    	//事前条件
    	selfTestListSaitenIkkatsuDeleteTargetPre(con);
    	
    	//情報生成
    	List list = doListSaitenIkkatsuDeleteTargetTeiki(con);
    	
    	//事後条件
    	selfTestListSaitenIkkatsuDeleteTargetTeikiPost(list);
    	
    	return list;
    }
    
    
    /**
     * 事前条件チェック
     * @param con
     */
    private void selfTestListSaitenIkkatsuDeleteTargetPre(
    									SaitenIkkatsuDeleteCondition con) {
    	if (con == null) {
    		throw new BusinessRuleException("検索条件がNULLです。");
    	}
    }
	
    /**
     * 情報生成
     * @param con
     * @return list
     */
    private List doListSaitenIkkatsuDeleteTargetTeiki(
    			SaitenIkkatsuDeleteCondition con) throws NoSuchDataException {
    
    	// DAOの取得
    	final UPDataAccessObject dao = getUPDataAccessObject();
    	
    	
    	List teikiList;
    	List tsuisaiList;
    	
    	// 動的SQLの生成
		final AbstractDynamicSQLContentsFactory factory =
			AbstractDynamicSQLContentsFactory.getFactory(
					AbstractDynamicSQLContentsFactory.KM00147);
		
		//定期試験
		final SQLHelper sqlHelper =
			new SQLHelper(factory.create(con));		
		
		SaitenIkkatsuDeleteExchanger ve = 
			new SaitenIkkatsuDeleteExchanger();

    	// SQLを設定
    	dao.setSQL(sqlHelper);
//        UtilLog.debug(this.getClass(),
//  			  "\n--- sqlHelper = [" + sqlHelper + "]");
        try {
			teikiList = 
				ve.listDeleteTargetTeiki(dao.find(
					ve.preparedStatementBindingValuesTeiki(con)));
        } finally {
        	//2007-01-17 クローズ漏れ対応
        	dao.clearSQL();
        }
			
		return teikiList;
    }
    
    /**
     * 事後条件チェック
     * @param list
     */
    private void selfTestListSaitenIkkatsuDeleteTargetTeikiPost(List list) {
    	if (list == null) { //ありえません
    		throw new BusinessRuleException("取得した情報がNULLです。");
    	}
    	
    	for (int i = 0; i < list.size(); i++ ) {
    		SaitenIkkatsuDeleteTargetDTO dto =
    			(SaitenIkkatsuDeleteTargetDTO)list.get(i);
    		
    		if (dto.getKaikoNendo() < 1900 || dto.getKaikoNendo() > 9999) {
    			throw new BusinessRuleException("開講年度が有効範囲外です。");
    		}
    		
    		if (dto.getGakkiNo() < 1 ) {
    			throw new BusinessRuleException("学期NOが0以下です。");
    		}
    		
    		if (dto.getJugyoCd() == null) {
    			throw new BusinessRuleException("授業コードがNULLです。");
    		}
    		
    	}
    }
    
    /**
     * 既に登録されている採点登録情報から削除対象となるデータを抽出します。(追再試験)
     * @param con
     * @return list
     */
    public List listSaitenIkkatsuDeleteTargetTsuisai(
    		SaitenIkkatsuDeleteCondition con) throws NoSuchDataException {
    	
    	//事前条件
    	selfTestListSaitenIkkatsuDeleteTargetPre(con);
    	
    	//情報生成
    	List list = doListSaitenIkkatsuDeleteTargetTsuisai(con);
    	
    	//事後条件
    	selfTestListSaitenIkkatsuDeleteTargetTsuisaiPost(list);
    	
    	return list;
    }
    
    	
    /**
     * 情報生成
     * @param con
     * @return list
     */
    private List doListSaitenIkkatsuDeleteTargetTsuisai(
    			SaitenIkkatsuDeleteCondition con) throws NoSuchDataException {
    
    	// DAOの取得
    	final UPDataAccessObject dao = getUPDataAccessObject();
    	
    	List tsuisaiList;
    	
    	// 動的SQLの生成
		final AbstractDynamicSQLContentsFactory factory =
			AbstractDynamicSQLContentsFactory.getFactory(
					AbstractDynamicSQLContentsFactory.KM00148);
		
		SaitenIkkatsuDeleteExchanger ve = 
			new SaitenIkkatsuDeleteExchanger();
       
		//追再試験
		final SQLHelper sqlHelper =
			new SQLHelper(factory.create(con));

    	// SQLを設定
    	dao.setSQL(sqlHelper);
//        UtilLog.debug(this.getClass(),
//  			  "\n--- sqlHelper = [" + sqlHelper + "]");
    	try {
			tsuisaiList =
				ve.listDeleteTargetTsuisai(dao.find(
					ve.preparedStatementBindingValuesTsuisai(con)));
    	} finally {
        	//2007-01-17 クローズ漏れ対応
        	dao.clearSQL();
        }
			
		return tsuisaiList;
    }
    
    /**
     * 事後条件チェック
     * @param list
     */
    private void selfTestListSaitenIkkatsuDeleteTargetTsuisaiPost(List list) {
    	if (list == null) { //ありえません
    		throw new BusinessRuleException("取得した情報がNULLです。");
    	}
    	
    	for (int i = 0; i < list.size(); i++ ) {
    		SaitenIkkatsuDeleteTargetDTO dto =
    			(SaitenIkkatsuDeleteTargetDTO)list.get(i);
    		
    		if (dto.getKaikoNendo() < 1900 || dto.getKaikoNendo() > 9999) {
    			throw new BusinessRuleException("開講年度が有効範囲外です。");
    		}
    		
    		if (dto.getGakkiNo() < 1 ) {
    			throw new BusinessRuleException("学期NOが0以下です。");
    		}
    		
    		if (dto.getJugyoCd() == null) {
    			throw new BusinessRuleException("授業コードがNULLです。");
    		}
    		
    	}
    }

// 2007/02/27 不具合管理一覧：No.3621 Start -->>

	/**
	 * 追再試評価リミットチェック
	 * @param	String		学籍番号
	 * @param	shikenFlg	試験区分
	 * @param	soten		素点
	 * @return	ret			追再試評価リミット結果：[0]結果／[1]素点MAX
	 */
	public int[] checkMaxSoten(	String	gakusekiCd,
									int		shikenFlg,
									int		soten) throws DbException, NoSuchDataException {

        // 学籍情報取得
		CobGaksekiUPDAO cobGaksekiUPDAO	= (CobGaksekiUPDAO)this.getDbSession().getDao(CobGaksekiUPDAO.class);
        CobGaksekiUPAR	cobGaksekiUPAR	= (CobGaksekiUPAR)cobGaksekiUPDAO.findCurrentByGakusekiCd(	gakusekiCd,
        																							UtilDate.cnvSqlDate(DateFactory.getInstance()));

		// 学生情報を取得
		GakuseiService		gakuseiSvc = new GakuseiService(this);
		GakuseiHeaderDTO	gakuseiDto = gakuseiSvc.acquireHeaderInfomation(new Long(cobGaksekiUPAR.getKanriNo()));

		// 追再試評価リミットチェック
		return checkMaxSoten(gakuseiDto,shikenFlg,soten);
	}

	/**
	 * 追再試評価リミットチェック
	 * @param	kanriNo		管理番号
	 * @param	shikenFlg	試験区分
	 * @param	soten		素点
	 * @return	ret			追再試評価リミット結果：[0]結果／[1]素点MAX
	 */
	public int[] checkMaxSoten(	long	kanriNo,
									int		shikenFlg,
									int		soten) throws DbException, NoSuchDataException {

		// 学生情報を取得
		GakuseiService		gakuseiSvc = new GakuseiService(this);
		GakuseiHeaderDTO	gakuseiDto = gakuseiSvc.acquireHeaderInfomation(new Long(kanriNo));

		// 追再試評価リミットチェック
		return checkMaxSoten(gakuseiDto,shikenFlg,soten);
	}

	/**
	 * 追再試評価リミットチェック
	 * @param	gakuseiDto	学生情報
	 * @param	shikenFlg	試験区分
	 * @param	soten		素点
	 * @return	ret			追再試評価リミット結果：[0]結果／[1]素点MAX
	 */
	public int[] checkMaxSoten(	GakuseiHeaderDTO	gakuseiDto,
									int					shikenFlg,
									int					soten) throws DbException {

		int ret[] = {MAX_SOTEN_CHK_NO_ERR,SOTEN_FROM};	// [0]結果／[1]素点MAX

		// 定期試験の場合、trueにてチェックを行わない。
        if(shikenFlg == ShikenUPKbn.TEIKISHIKEN.getCode()) {
			return ret;
		}

        // 評価基準情報取得
        List hykList = getHykList(gakuseiDto);

        // 評価リミットのランク以下をマップに格納
        Map			hykLimitMap			= new LinkedHashMap();	// 評価リミットMap
        boolean	hykLimitOver		= false;				// 評価リミット超過フラグ
        int			maxSotenLimit		= -1;					// 評価リミットでの評価MAX素点
        int			maxSotenLimitFromto	= -1;					// 評価リミットでSOTEN_FROM ～ SOTEN_TO間での評価MAX素点
        Iterator it =hykList.iterator();
		while(it.hasNext()){
			KmzHykUPAR kmzHykUPAR = (KmzHykUPAR)it.next();

			// 評価リミットを超過していない場合は、評価リミットMapに格納
			if(!hykLimitOver) {
				hykLimitMap.put(kmzHykUPAR.getHyokaCd(),kmzHykUPAR);
				// 追試
				if(shikenFlg == ShikenUPKbn.TSUISHIKEN.getCode()) {
					if(kmzHykUPAR.isTuisiHyokaMax()) {
						hykLimitOver = true;
					}
				// 再試
				} else if(shikenFlg == ShikenUPKbn.SAISHIKEN.getCode()) {
					if(kmzHykUPAR.isSaisiHyokaMax()) {
						hykLimitOver = true;
					}
				}

				// 評価リミットでの評価MAX素点
				maxSotenLimit = kmzHykUPAR.getSotenTo().intValue();

				// 評価リミットでSOTEN_FROM ～ SOTEN_TO間での評価MAX素点を取得
				if(kmzHykUPAR.getSotenTo().intValue() <= SOTEN_TO) {
					maxSotenLimitFromto = kmzHykUPAR.getSotenTo().intValue();
				}

				// 評価リミットありならループ処理終了
				if(hykLimitOver) {
					
				}
			}
		}

		// この時点で評価リミット超過フラグ【false】は評価リミットが設定されていないとみなす。
		// その場合、以降の処理は行わない為、ここで処理終了
		if(!hykLimitOver) {
			return ret;
		}

		// 追再試評価リミットチェック
		ret = checkMaxSoten(soten,hykList,hykLimitMap,maxSotenLimit,maxSotenLimitFromto);

		return ret;
    }

	/**
	 * 評価基準情報（評価ランク降順）を取得する
	 * @param	gakuseiDto	学生情報
	 * @return	hykList		評価基準情報
	 */
	private List getHykList(GakuseiHeaderDTO gakuseiDto) throws DbException {

        List hykList = null;

        // 評価基準情報取得
        KmzHykUPDAO	kmzHykUPDAO	= (KmzHykUPDAO)this.getDbSession().getDao(KmzHykUPDAO.class);
     				hykList		= kmzHykUPDAO.findByHyokaCombo(	gakuseiDto.getMinashiNyugakuNendo().intValue(),
     															gakuseiDto.getMinashiNyugakuGakkiNo().intValue(),
																gakuseiDto.getCurriculumGakkaCode());

		// 評価ランク降順にソート
		KmzHykARComparator sort = new KmzHykARComparator();
		sort.desc(KmzHykARComparator.HYOKA_RANK);
		Collections.sort(hykList,sort);

		return hykList;
	}

	/**
	 * 追再試評価リミットチェック
	 * @param	soten				素点
	 * @param	hykList				評価基準情報
	 * @param	hykLimitMap			評価リミットMap
	 * @param	maxSotenLimit		評価リミットでの評価MAX素点
	 * @param	maxSotenLimitFromto	評価リミットでSOTEN_FROM ～ SOTEN_TO間での評価MAX素点
	 * @return	ret					追再試評価リミット結果：[0]結果／[1]素点MAX
	 */
	private int[] checkMaxSoten(	int		soten,
									List	hykList,
									Map		hykLimitMap,
									int		maxSotenLimit,
									int		maxSotenLimitFromto) {

		int ret[] = {MAX_SOTEN_CHK_NO_ERR,SOTEN_FROM};	// [0]結果／[1]素点MAX

		// 素点に該当する評価コードを取得
		String hykCd = getHykCd(soten,hykList);

		// 上記までで必要な情報を取得した為、下記以降はチェック処理実施
		// 評価リミット内に素点が存在する場合はチェックOK
		if((KmzHykUPAR)hykLimitMap.get(hykCd) != null) {
			return ret;
		}

		if(soten >= SOTEN_FROM && soten <= SOTEN_TO) {
        	ret[0] = MAX_SOTEN_CHK_ERR_1;
        	ret[1] = maxSotenLimitFromto;
			return ret;
		}

		// 素点がSOTEN_FROM～SOTEN_TOの範囲外
        ret[0] = MAX_SOTEN_CHK_ERR_2;
        ret[1] = maxSotenLimit;
		return ret;
    }

	/**
	 * 素点から評価コードを取得する
	 * @param	soten	素点
	 * @param	hykList	評価基準情報
	 * @return	hykCd	評価コード
	 */
	public String getHykCd(	int		soten,
							List	hykList) {

		String hykCd = null;	// 評価コード

		// 素点に該当する評価コードを取得
		Iterator it = hykList.iterator();
		while(it.hasNext()){
			KmzHykUPAR kmzHykUPAR = (KmzHykUPAR)it.next();
			if(		soten >= kmzHykUPAR.getSotenFrom().intValue()
				&&	soten <= kmzHykUPAR.getSotenTo().intValue()) {
				hykCd = kmzHykUPAR.getHyokaCd();
				break;
			}
		}

		return hykCd;
	}

	/**
	 * 追再試評価リミットチェック時のエラーメッセージを取得する
	 * @param	ret	追再試評価リミット結果：[0]結果／[1]素点MAX
	 * @return	msg	メッセージ
	 */
	public String getCheckMaxSotenErrMsg(int ret[]) {

		String msg = null;

		// エラーなし
		if(ret[0] == MAX_SOTEN_CHK_NO_ERR) {
			return msg;
		// リミット超過で素点が【SOTEN_TO】以下
		} else if(ret[0] == MAX_SOTEN_CHK_ERR_1) {
			msg = UtilProperty.getMsgString(KmMsgConst.KMC_MSG_0008E, String.valueOf(ret[1]));
			return msg;
		// リミット超過で素点が【SOTEN_TO】より大きい
		} else if(ret[0] == MAX_SOTEN_CHK_ERR_2) {
			msg = UtilProperty.getMsgString(KmMsgConst.KMC_MSG_0020E);
			return msg;
		}

		return msg;
	}
// <<-- End   2007/02/27 不具合管理一覧：No.3621
}