/*
 * 作成日: 2006/04/10
 */
package com.jast.gakuen.up.km.action;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jast.gakuen.framework.GakuenException;
import com.jast.gakuen.framework.PageCodeBaseEx;
import com.jast.gakuen.framework.constant.SyMsgConst;
import com.jast.gakuen.framework.db.DbException;
import com.jast.gakuen.framework.util.UtilCrypt;
import com.jast.gakuen.framework.util.UtilDate;
import com.jast.gakuen.framework.util.UtilLog;
import com.jast.gakuen.framework.util.UtilProperty;
import com.jast.gakuen.framework.util.UtilStr;
import com.jast.gakuen.framework.util.UtilSystem;
import com.jast.gakuen.system.co.constant.code.SaitenKbn;
import com.jast.gakuen.system.co.constant.code.ShikenUPKbn;
import com.jast.gakuen.system.co.constant.code.YomiganaKbn;
import com.jast.gakuen.system.co.db.dao.CobGaksekiDAO;
import com.jast.gakuen.system.co.db.dao.CoiJinjDAO;
import com.jast.gakuen.system.co.db.entity.CobGaksekiAR;
import com.jast.gakuen.system.co.db.entity.CoiJinjAR;
import com.jast.gakuen.system.km.db.dao.KmdJugyDAO;
import com.jast.gakuen.system.km.db.entity.KmbHykHaiAR;
import com.jast.gakuen.system.km.db.entity.KmdJgkmARComparator;
import com.jast.gakuen.system.km.db.entity.KmdJugyAR;
import com.jast.gakuen.system.km.db.entity.KmzHykAR;
import com.jast.gakuen.up.UpActionBase;
import com.jast.gakuen.up.co.bean.SearchUserBean;
import com.jast.gakuen.up.co.business.GakuseiInfoLinkChecker;
import com.jast.gakuen.up.co.constant.CoMsgConst;
import com.jast.gakuen.up.co.constant.UpActionConst;
import com.jast.gakuen.up.co.db.dao.CouParamDAO;
import com.jast.gakuen.up.co.db.entity.CouParamAR;
import com.jast.gakuen.up.co.exception.AlreadyUpdateException;
import com.jast.gakuen.up.co.exception.AlreadyUpdatePossibilityException;
import com.jast.gakuen.up.co.exception.GakuenSystemException;
import com.jast.gakuen.up.co.exception.NoSuchDataException;
import com.jast.gakuen.up.co.service.GakuseiService;
import com.jast.gakuen.up.co.service.IdoShutsugakuInformationDTO;
import com.jast.gakuen.up.co.service.JugyoService;
import com.jast.gakuen.up.co.service.SaitenService;
import com.jast.gakuen.up.co.service.SeisekiService;
import com.jast.gakuen.up.co.service.SettingValueService;
import com.jast.gakuen.up.co.service.ShukketsuService;
import com.jast.gakuen.up.co.util.DateFactory;
import com.jast.gakuen.up.co.util.UtilCosOpt;
import com.jast.gakuen.up.co.util.UtilUpMsg;
import com.jast.gakuen.up.co.util.UtilUpSystem;
import com.jast.gakuen.up.co.util.dto.GakuseiIdoCondition;
import com.jast.gakuen.up.co.util.dto.HyokaCondition;
import com.jast.gakuen.up.co.util.dto.HyokaKoshinValue;
import com.jast.gakuen.up.co.util.dto.JogaiTaishoIdoKubunCondition;
import com.jast.gakuen.up.co.util.dto.JugyoRishuCondition;
import com.jast.gakuen.up.co.util.dto.JugyoRisyuDTO;
import com.jast.gakuen.up.co.util.dto.RowCountCondition;
import com.jast.gakuen.up.co.util.dto.ShikenBetuHyokaKijunCondition;
import com.jast.gakuen.up.co.util.dto.ShukkessekiDTO;
import com.jast.gakuen.up.co.util.dto.ShukkessekiInfoCondition;
import com.jast.gakuen.up.co.util.dto.ShukketsuSetteiCondition;
import com.jast.gakuen.up.co.util.dto.ShussekiRitsuCondition;
import com.jast.gakuen.up.co.util.dto.SotenRangeCondition;
import com.jast.gakuen.up.co.util.vo.ShussekiRitsuVO;
import com.jast.gakuen.up.jg.Jgc91101A;
import com.jast.gakuen.up.jg.Jgc91102A;
import com.jast.gakuen.up.jg.business.JgaUtil;
import com.jast.gakuen.up.km.Kmc00201A;
import com.jast.gakuen.up.km.Kmc00202A;
import com.jast.gakuen.up.km.Kmc00202B;
import com.jast.gakuen.up.km.PKmc0203A;
import com.jast.gakuen.up.km.bean.Kmc00201AL02Bean;
import com.jast.gakuen.up.km.bean.Kmc00202AL01Bean;
import com.jast.gakuen.up.km.constant.KmMsgConst;
import com.jast.gakuen.up.km.db.dao.KmbHykHaiUPDAO;
import com.jast.gakuen.up.km.db.dao.KmcStnUnyoDAO;
import com.jast.gakuen.up.km.db.dao.KmdJgkmUPDAO;
import com.jast.gakuen.up.km.db.dao.KmzHykUPDAO;
import com.jast.gakuen.up.km.db.entity.KmcStnUnyoAR;
import com.jast.gakuen.up.km.db.entity.KmdJgkmUPAR;

/**
 * 採点登録画面用アクション <br>
 * 
 * @author JApan System Techniques Co.,Ltd. <br>
 */
public class Kmc00202AAct extends UpActionBase {

    /** 表示ボタン */
    public static final String ACTION_DISPLAY = "display";
//障害NO4082 出欠パフォーマンスアップ共通化 2007.08.20 Horiguchi Start
    private List ShukkessekiList = null;
//障害NO4082 出欠パフォーマンスアップ共通化 2007.08.20 Horiguchi End
    
// 障害対応 UPEX-153 採点登録画面の出欠情報を授業単位で表示する為追加 add Start
    private List PearentShukkessekiList = null;	// 出欠席情報リスト格納用リスト
    private Map SyuYaKuMap = null;					// 出欠率集約用マップ
// 障害対応 UPEX-153 採点登録画面の出欠情報を授業単位で表示する為追加 add End
    
    private String sotenAtukai = "";
    private String misaitenChk = "";
    public boolean semesterDsp = false;
    public static final String SOTEN = "0";
    public static final String HYOKA = "1";
    public static final String NO_CHK = "0";
    public static final String CHK = "1";
    public static final String NO_DSP = "0";
    public static final String DSP = "1";
    
    // 評価コードを表示するか、評価略称を表示するか
    // true:評価コード false:評価略称
    private boolean isDspHyoka = true;
    
    /**
     * 画面の初期表示を行います <br>
     * 
     * @param pagecode
     *            ページコード
     * @return トランザクション処理の結果
     */
    protected String init(PageCodeBaseEx pagecode) {

        Kmc00202A pc = (Kmc00202A) pagecode;
        Kmc00201A pagePc = (Kmc00201A) UtilSystem
                .getManagedBean(Kmc00201A.class);
        
        if (pc instanceof Jgc91102A) {
        	 pagePc = (Kmc00201A) UtilSystem
             .getManagedBean(Jgc91101A.class);
        }

        // パラメータの取得
        setParam();
        
		// 確定押下時に未採点登録データ存在チェックを行う
		pc.getPropExecutableFixed().setIntegerValue(new Integer(0));

        //表示並び順セット
        narabijunBind(pc);

        // 総計グラフ表示判定
        setSokeiGraphFlag(pc);

// 2008-02-08 UPEX-250 START        
        //学科表示区分の取得
        setDspGakka(pc);
// 2008-02-08 UPEX-250 END        

        //画面の初期のデータを取る
        setData(pagePc, pc);
        
        //子画面のフォーカス用フラグの初期化
        pc.setFocusFlg(true);
        
        // 登録処理時で、複数画面の使用により、画面とサーバの内容が異なっていないかをチェックするため、
        // 画面に隠し項目（年度、学期、授業コード、試験区分、試験回数の結合文字列）を設定
        pc.getPropBrowserHidden().setStringValue(pc.getPropNendo().getStringValue() 
        		+ "|" + pc.getPropGakki().getStringValue()+ "|" + pc.getJugyoCd() 
				+ "|" + pc.getPropShikenKubun().getStringValue() + "|" + UtilStr.cnvNull(pc.getShikenKaisu()));
        
        return UpActionConst.RET_TRUE;
    }

    /**
	 * パラメータテーブルより素点扱い方法、未採点者チェック、セメスタの表示可否の設定値を取得します。<br>
	 */
	private void setParam() {

		try {
			// パラメータDAO
			CouParamDAO couParamDAO = (CouParamDAO) getDbs().getDao(CouParamDAO.class);
			CouParamAR couParamAR;
			
			// 素点扱い方法
			couParamAR = couParamDAO.findByPrimaryKey("KMC", "SOTEN_ATUKAI_FLG", 0);
			sotenAtukai = "0";
			if (couParamAR != null) {
				if (!UtilStr.cnvNull(couParamAR.getValue()).equals("")
		        && (couParamAR.getValue().equals(SOTEN)
		         || couParamAR.getValue().equals(HYOKA))) {
		        	sotenAtukai = couParamAR.getValue();
		        }
			}

			// 未採点者チェック
			couParamAR = couParamDAO.findByPrimaryKey("KMC", "MISAITEN_MSG_FLG", 0);
			misaitenChk = "0";
			if (couParamAR != null) {
				if (!UtilStr.cnvNull(couParamAR.getValue()).equals("")
		        && (couParamAR.getValue().equals(CHK)
		         || couParamAR.getValue().equals(NO_CHK))) {
					misaitenChk = couParamAR.getValue();
		        }
			}
			
			// セメスタの表示可否
			couParamAR = couParamDAO.findByPrimaryKey("KMC", "DSP_SEMESTER", 0);
			semesterDsp = false;
			if (couParamAR != null) {
				if (!UtilStr.cnvNull(couParamAR.getValue()).equals("")) {
					if (couParamAR.getValue().equals(NO_DSP)) {
						semesterDsp = false;
					} else if (couParamAR.getValue().equals(DSP)) {
						semesterDsp = true;
					}
		        }
			}
			
			// 評価コードを表示するか、評価略称を表示するか
			couParamAR = couParamDAO.findByPrimaryKey("KMC", "DSP_HYOKA", 0);
			if (couParamAR != null) {
				if (!UtilStr.cnvNull(couParamAR.getValue()).equals("")) {
					if (couParamAR.getValue().equals("0")) {
						isDspHyoka = true;
					} else if (couParamAR.getValue().equals("1")) {
						isDspHyoka = false;
					}
		        }
			}
			
			
		} catch (DbException e) {
			throw new RuntimeException(e);
		}	        
	}

    /**
     * 評価割合画面表示を行います。 <br>
     * 
     * @param pagecode
     *            ページコード
     * @return トランザクション処理の結果
     */
    protected String hyokaDisplay(PageCodeBaseEx pagecode) {

    	Kmc00202A pc = (Kmc00202A) pagecode;
	    Kmc00201A pagePc = (Kmc00201A) UtilSystem
	            .getManagedBean(Kmc00201A.class);
	    
        // 画面の隠し項目（年度、学期、授業コード、試験区分、試験回数の結合文字列）を取得
        String browserHidden = pc.getPropBrowserHidden().getStringValue();
        // サーバの項目（年度、学期、授業コード、試験区分、試験回数の結合文字列）を取得
        String pcItem = pc.getPropNendo().getStringValue() + "|" +  pc.getPropGakki().getStringValue() 
							+ "|" +  pc.getJugyoCd() + "|" +  pc.getPropShikenKubun().getStringValue() 
							+ "|" +  UtilStr.cnvNull(pc.getShikenKaisu());
        // 複数画面の使用により、画面とサーバの内容が異なっていないかをチェック
        if (!pcItem.equals(browserHidden)) {
            UtilSystem.getDisplayInfo().setDisplayMessage(UtilUpMsg.editMsg(
            				UtilProperty.getMsgString(CoMsgConst.CO_MSG_0060E)));
            pc.setContentRendered(false);
            return UpActionConst.RET_FALSE;
        }
	    
	    if (pc instanceof Jgc91102A) {
	   	 pagePc = (Kmc00201A) UtilSystem
	        .getManagedBean(Jgc91101A.class);
	    }

        // 評価略称再セット
        resethyokaName(pc);
	    
        final StringBuffer rowClass = new StringBuffer();
        final SaitenService saitenService = new SaitenService(this);
        final JugyoService jugyoService = new JugyoService(this);
        
        //空行対応
        final List dataList = pc.getPropSaitenTorokuTable().getNoEmptyList();

        int rowCnt = getRow(pc);

        int errCount = 0;

		final Iterator ite = dataList.iterator();
        //画面のデーターが取る
        for (int i = 0; ite.hasNext(); i++) {
            final Kmc00202AL01Bean listBean = (Kmc00202AL01Bean) ite.next();
            final HyokaKoshinValue hyokaKoshinValue = new HyokaKoshinValue();
            hyokaKoshinValue.setKaikoNendo(Integer.valueOf(pagePc
                    .getPropNendo().getStringValue()));
            hyokaKoshinValue.setGakkiNo(Integer.valueOf(pagePc.getPropGakkiNo()
                    .getStringValue()));
            hyokaKoshinValue.setJugyoCd(pc.getJugyoCd());
            hyokaKoshinValue.setKanriNo(listBean.getKanriNo());
            final String kyoinCode = UtilUpSystem.getUpSystemData()
                    .getLoginUserBean().getJinjiCd();
            hyokaKoshinValue.setSitnJinjiCd(kyoinCode);

            //定期試験以外試験回数セット
            if (pc.getShikenKaisu() != null) {
                hyokaKoshinValue.setSikenKaisu(Integer.valueOf(pc
                        .getShikenKaisu()));
            }
            hyokaKoshinValue.setSitnUpdateDate(DateFactory.getInstance());

            //素点運用の場合
            hyokaKoshinValue.setSaitenKbn(String.valueOf(SaitenKbn.SOTEN
                    .getCode()));

            // チェックのメッセージ
            String message = "";

            //素点の入力チェックを行う
            message = acquireCheckMsg(jugyoService, listBean,
                    saitenService, pagePc);

            listBean.setMessage(message);

            //エラー数をカウント
            if (message.length() > 0) {
                errCount++;
            }
            //エラーメッセージがない、または素点未入力は背景白
            if (message.length() != 0
                    || (listBean.getSoten() == null
                    || UtilStr.cnvTrim(listBean.getSoten()).length() == 0)) {

                rowClass.append(", rowClass1");
            } else {
                rowClass.append(", selectiveLine");
            }
        }

        if (errCount != 0) {
            // メッセージ「エラーがありますので確認してください」
            UtilSystem.getDisplayInfo().setDisplayMessage(
                    UtilUpMsg.editMsg(UtilProperty
                            .getMsgString("KMC_MSG_0009E")));
            return UpActionConst.RET_TRUE;
        }

        //オプションテーブルに検索条件を保持させる。
        saveDefaultItemValue(pc);

		// 子画面起動フラグをONにする。
		pc.getPropKogamenOpenFlg().setStringValue("1");
 
        return UpActionConst.RET_TRUE;
    }

    /**
     * 採点一覧プレビュー画面表示を行います。 <br>
     * 
     * @param pagecode
     *            ページコード
     * @return トランザクション処理の結果
     */
    protected String saitenDisplay(PageCodeBaseEx pagecode) {

    	Kmc00202A pc = (Kmc00202A) pagecode;
   	
        //オプションテーブルに検索条件を保持させる。
        saveDefaultItemValue(pc);

    	PKmc0203A.sotenInsatuOpen();
		pc.setFocusFlg(false);
		
		return UpActionConst.RET_TRUE;
    }

    /**
     * 採点一覧プレビュー画面表示を行います。(クラスプロファイル用) <br>
     * 
     * @param pagecode
     *            ページコード
     * @return トランザクション処理の結果
     */
    protected String saitenDisplayForClass(PageCodeBaseEx pagecode) {

    	Kmc00202A pc = (Kmc00202A) pagecode;

        //オプションテーブルに検索条件を保持させる。
        saveDefaultItemValue(pc);

    	PKmc0203A.sotenInsatuOpenForClass();
		pc.setFocusFlg(false);
		
		return UpActionConst.RET_TRUE;
    }
	
//  V1.2対応 2009/11/22 k.higashida Start
	/**
	 * 素点の場合、画面の追再試験から、採点登録画面のデータを表示します。<br>
	 * 
	 * @param pc 採点授業一覧のページコード
	 * @param nextPc 採点登録画面のページコード
	 * @param shikenFlg true:追試験 false:再試験
	 * @return 試験名称と試験回数
	 */
	private String getSotenTuiSaiShikenValue(Kmc00201A pc,
			Kmc00202A nextPc, boolean shikenFlg) {
		//試験名称：追試験、再試験
		String shikenName = "";
		//試験回数
		String shikenKaisu;
		
		final String kaisu = UtilProperty.getMsgItemString("CO_Times");
		Kmc00201AL02Bean bean = null;
		
		if (shikenFlg) {
			//itemKM_ja.propertiesファイルから ”追試験”を取得します。
//			 V1.2対応 2009/11/22 k.higashida Start
//			shikenName = UtilProperty.getMsgItemString("KM_TuiShiken");
			if (pc.isUnyoFlg()) {
				shikenName = UtilProperty.getMsgItemString("KM_TuiShiken");
			} else {
				shikenName = UtilProperty.getMsgItemString("KM_TuiShikenGai");
			}
			if (nextPc instanceof Jgc91102A) {
				bean = 
					(Kmc00201AL02Bean) pc.getPropTuiShikenTable().getList().get(0);
				if (chkUnyo(bean.getJugyoCode(),
						Integer.valueOf(pc.getNendo()).intValue(),
						Integer.valueOf(pc.getPropGakkiNo().getStringValue()).intValue(),
						String.valueOf(ShikenUPKbn.TSUISHIKEN.getCode()).toString(),
						Integer.valueOf(bean.getShikenKaisu()).intValue())) {
					shikenName = UtilProperty.getMsgItemString("KM_TuiShiken");
				} else {
					shikenName = UtilProperty.getMsgItemString("KM_TuiShikenGai");
				}
			} else {
//				 V1.2対応 2009/11/22 k.higashida End
				bean = (Kmc00201AL02Bean) pc.getPropTuiShikenTable().getRowData();
			}
		}
		
		shikenKaisu = bean.getShikenKaisu();
		
		nextPc.setJugyoCd(bean.getJugyoCode());
		nextPc.setShikenKaisu(bean.getShikenKaisu());
		nextPc.getPropKamokumei().setStringValue(bean.getKamokName());
		//// 6/22 不具合対応 Start↓↓↓↓↓
		nextPc.getPropKamokumei().setStringValue(bean.getJugyoKamokName());
		//// 6/22 不具合対応 End↑↑↑↑↑↑		
		if (shikenKaisu == null) {
			shikenKaisu = "";
		} else {
			shikenKaisu = "(" + shikenKaisu + kaisu + ")";
		}
		
		shikenName = shikenName + shikenKaisu;
		return shikenName;
	}
	
	/**
	 * 採点運用外チェックを行います。<br>
	 * 
	 * @param JugyoCd 授業コード
	 * @param kaikoNendo 開講年度
	 * @param gakkiNo 学期No
	 * @param shikenKbn 試験区分
	 * @param shikenKaisu 試験回数
	 * @return 運用内:true、運用外:false
	 */
	private boolean chkUnyo(String jugyoCd, int kaikoNendo,
							  int gakkiNo, String shikenKbn, int shikenKaisu) {
		
		boolean booRet = false;

		// 現在日
		Calendar cal = Calendar.getInstance();
		Date today = new Date(cal.getTimeInMillis()); 

        //　採点運用DAO
    	KmcStnUnyoDAO kmcStnUnyoDAO = (KmcStnUnyoDAO)getDbs().
											getDao(KmcStnUnyoDAO.class);
        // 採点運用AR
    	KmcStnUnyoAR kmcStnUnyoAR;
		
        // 授業DAO
    	KmdJugyDAO kmdJugyDAO = (KmdJugyDAO)getDbs().
											getDao(KmdJugyDAO.class);
        // 授業AR
    	KmdJugyAR kmdJugyAR;
		
		try {
			// 授業の部署コードを取得
			kmdJugyAR = kmdJugyDAO.findByPrimaryKey(kaikoNendo, jugyoCd);
			
			if (kmdJugyAR != null) {
				kmcStnUnyoAR = kmcStnUnyoDAO.findByPrimaryKey(
						kmdJugyAR.getKanriBsyoCd(),
						kaikoNendo,
						gakkiNo,
						shikenKbn,
						shikenKaisu);
				if (kmcStnUnyoAR != null
				 && today.compareTo(kmcStnUnyoAR.getTorokuKaishibi()) >= 0
				 && today.compareTo(kmcStnUnyoAR.getTorokuShuryobi()) <= 0) {
					// 採点運用期間内
					booRet = true;
				} else {
					// 採点運用期間外
					booRet = false;
				}
			}				
				
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		}
		return booRet;
		
	}
//  V1.2対応 2009/11/22 k.higashida End

	
    /**
     * 再検索処理を行います。 <br>
     * 登録、更新画面より一覧画面に戻します。
     * 
     * @param pagecode
     *            ページコード
     * @return トランザクション処理の結果
     */
    protected String display(PageCodeBaseEx pagecode) {
        Kmc00202A pc = (Kmc00202A) pagecode;
        Kmc00201A pagePc = (Kmc00201A) UtilSystem
                .getManagedBean(Kmc00201A.class);
        
        if (pc instanceof Jgc91102A) {
       	 pagePc = (Kmc00201A) UtilSystem
            .getManagedBean(Jgc91101A.class);
       }
        
        //オプションテーブルに検索条件を保持させる。
        saveDefaultItemValue(pc);
        //検索データを取る
        setData(pagePc, pc);
        pc.getPropValueChanged().setValue(Boolean.FALSE);
        pc.getPropHasInputError().setValue(Boolean.FALSE);
        // 6/22修正Start↓↓↓↓↓↓↓
        // return UpActionConst.RET_TRUE;
        pc.getPropSaitenTorokuTable().setFirst(0);
        
        if (pc.getHtmlSaitenTorokuTable() !=  null) {
        	pc.getHtmlSaitenTorokuTable().setFirst(0);
        }
        
        return pc.getFormId();
        // 6/22修正End↑↑↑↑↑↑↑↑
    }

    /**
     * データを作成します。 <br>
     * 
     * @param pagePc
     *            Kmc00201Aページコード
     * @param pc
     *            Kmc00202Aページコード
     */
    protected void setData(Kmc00201A pagePc, Kmc00202A pc) {
        final List rowList = new ArrayList();
        final StringBuffer rowClass = new StringBuffer();
        final List listData = getJyugyoRisyu(pagePc, pc);

        //空白行を件数分表示
        pc.getPropSaitenTorokuTable().setListbean(new Kmc00202AL01Bean());

// 2008-02-08 UPEX-250 START
		// 学科種別区分を取得
		String dspGakka = pc.getDspGakka();		
		// ラベル設定
		if (dspGakka.equals("0")) {
			// 所属学科組織を表示
			pc.setGakkaLabel(UtilProperty.getMsgItemString("KM_ShozokuGakka"));
		} else {
			// カリキュラム学科組織を表示
			pc.setGakkaLabel(UtilProperty.getMsgItemString("KM_Gakka"));
		}
// 2008-02-08 UPEX-250 END

        // 除外対象の異動区分を取得
        final SaitenService saitenService = new SaitenService(this);
        
// ▽▽ 2009.10.05 h.matsuda 障害対応UPEX-1126 del start	        
//        final List jogaiTaishoIdoKubunList = saitenService
//                .listJogaiTaishoIdoKubun(makeJogaiTaishoIdoKubunCondition(
//                        pagePc, pc));
// △△ 2009.10.05 h.matsuda 障害対応UPEX-1126 del end 
        // 現在日付で学生の異動出学情報を検索
        final GakuseiService gakuseiService = new GakuseiService(this);
        final Map idoShutsugakuInformationMap = searchIdoShutsugakuInformations(gakuseiService);

        //画面のデータを取る
        if (listData != null && listData.size() > 0) {
            final SeisekiService seisekiService = new SeisekiService(this);
//障害NO4082 出欠パフォーマンスアップ共通化 2007.08.20 Horiguchi Start
            ShukkessekiList = null;
            //出欠管理する場合
            if (getAcquireShukketsuKanriBoolean(pagePc, pc)) {
                ShukkessekiList = new ArrayList();
	            try{
// 障害対応 UPEX-153 採点登録画面の出欠率を授業単位で表示する為修正 2009.02.04 Takemoto Start
//		            //授業年度(授業開始年度)取得
//					KmdJgkmUPDAO jugyKmDAO =
//						(KmdJgkmUPDAO)this.getDbSession().getDao(KmdJgkmUPDAO.class);
//					List KmdJgkmList = jugyKmDAO.findByKaikoNendoGakkiNoJugyoCd(
//												Integer.parseInt(pagePc.getNendo()),
//												Integer.parseInt(pagePc.getPropTempgakkiNo().getStringValue()),
//												pc.getJugyoCd());
//					if (KmdJgkmList != null && KmdJgkmList.size() != 0) {
//					    KmdJgkmUPAR jugyKmAR = (KmdJgkmUPAR)KmdJgkmList.get(0);
//						
//				        ShukketsuService shukketsuService = new ShukketsuService(this);
//				        ShukkessekiInfoCondition condition  = new ShukkessekiInfoCondition ();
//				        condition.setNendo(new Integer(pagePc.getNendo()));
//				        condition.setGakkiNo(Integer.valueOf(pagePc.getPropTempgakkiNo().getStringValue()));
//				        condition.setJugyoCd(pc.getJugyoCd());
//				        condition.setJinjiCd(pagePc.getKyoinCd());
//				        condition.setKijunBi(UtilDate.cnvSqlDate(DateFactory.getInstance()));
//				        condition.setJugyoNendo(new Integer(jugyKmAR.getNendo()));
//				        condition.setJkwrSanshoKbn(new Integer(JgaUtil.checkJikanwariSansho(this.getDbs())));
//				        condition.setSortKey(new Integer(0));
//						try{
//						    //授業に対する出欠情報取得
//							ShukkessekiList = shukketsuService.listShukkessekiInfo(condition);
//						}
//				        catch(Exception e){
//				            UtilLog.error( this.getClass(),"出欠情報取得不可");
//				        }
	            	
	            	// ==============================================================
					// 対象授業の学期数分の出欠情報を取得する
	            	// ==============================================================
		            // 授業年度(授業開始年度)取得
					KmdJgkmUPDAO jugyKmDAO = (KmdJgkmUPDAO)this.getDbSession().getDao(KmdJgkmUPDAO.class);
					List JgkmListForJugyoNendo = jugyKmDAO.findByKaikoNendoGakkiNoJugyoCd(
												Integer.parseInt(pagePc.getNendo()),
												Integer.parseInt(pagePc.getPropTempgakkiNo().getStringValue()),
												pc.getJugyoCd());
					
					if (JgkmListForJugyoNendo != null && JgkmListForJugyoNendo.size() != 0) {
						
						// 授業コマから授業年度を取得し、授業年度、授業コードに紐付くコマの取得を行う
						KmdJgkmUPAR jugyKmARForNendo = (KmdJgkmUPAR)JgkmListForJugyoNendo.get(0);
						int jugyoNendo = jugyKmARForNendo.getNendo();
						
						List jugyKmList = jugyKmDAO.findByNendoJugyoCd(jugyoNendo,pc.getJugyoCd());
						if( jugyKmList != null && !jugyKmList.isEmpty() ){
							
					        KmdJgkmARComparator sort = new KmdJgkmARComparator();
					        sort.asc(KmdJgkmARComparator.NENDO);
					        sort.asc(KmdJgkmARComparator.JUGYO_CD);
					        sort.asc(KmdJgkmARComparator.KAIKO_NENDO);
					        sort.asc(KmdJgkmARComparator.GAKKI_NO);
					        java.util.Collections.sort(jugyKmList, sort);
					        
							// 出欠席リストを格納するリスト
							PearentShukkessekiList = new ArrayList();
							
							// 年度、学期の前回値格納用変数
							int bfNendo = 0;
							int bfGakki = 0;
							
							// 対象授業の学期数分の出欠情報を取得する
							for( int i = 0 ; i < jugyKmList.size() ; i++ ){
								
								KmdJgkmUPAR jugyKmAR = (KmdJgkmUPAR)jugyKmList.get(i);
								
								// 開講年度と学期が前回値と同じであればスキップ
								if( jugyKmAR.getKaikoNendo() == bfNendo 
										&& jugyKmAR.getGakkiNo() == bfGakki ){
									continue;
								}
								
								bfNendo = jugyKmAR.getKaikoNendo();
								bfGakki = jugyKmAR.getGakkiNo();
								
						        ShukketsuService shukketsuService = new ShukketsuService(this);
						        ShukkessekiInfoCondition condition  = new ShukkessekiInfoCondition ();
						        condition.setNendo(new Integer(jugyKmAR.getKaikoNendo()));
						        condition.setGakkiNo(new Integer(jugyKmAR.getGakkiNo()));
						        condition.setJugyoCd(pc.getJugyoCd());
						        condition.setJinjiCd(pagePc.getKyoinCd());
						        condition.setKijunBi(UtilDate.cnvSqlDate(DateFactory.getInstance()));
						        condition.setJugyoNendo(new Integer(jugyKmAR.getNendo()));
						        condition.setJkwrSanshoKbn(new Integer(JgaUtil.checkJikanwariSansho(this.getDbs())));
						        condition.setSortKey(new Integer(0));
								try{
								    //授業に対する出欠情報取得
									ShukkessekiList = shukketsuService.listShukkessekiInfo(condition);
								}
						        catch(Exception e){
						            UtilLog.error( this.getClass(),"出欠情報取得不可");
						        }
						        // 取得した出欠席リストを格納
						        PearentShukkessekiList.add(ShukkessekiList);
							}
							
							// 集計処理を行う
							shuyakuSyukkesseki();
							
						}else {
						    UtilLog.error( this.getClass(), "授業コマ取得不可" );
			            }
// 障害対応 UPEX-153 採点登録画面の出欠率を授業単位で表示する為修正 2009.02.04 Takemoto End
					} else {
					    UtilLog.error( this.getClass(), "授業コマ取得不可" );
		            }
	            } catch (Exception e) {
	                UtilLog.error( this.getClass(), "授業コマ取得不可" );
	            }
            }
//障害NO4082 出欠パフォーマンスアップ共通化 2007.08.20 Horiguchi End
            final Iterator ite = listData.iterator();
            for (int i = 0; ite.hasNext(); i++) {
                final JugyoRisyuDTO dto = (JugyoRisyuDTO) ite.next();

                boolean jogaiTaisho = false;
                String biko = "";

                final Long kanriNo = dto.getKanriBangou();
                if (kanriNo != null) {
                    // 処理機能記述:【2-2でレコードが取得された場合】
                    if (idoShutsugakuInformationMap.containsKey(kanriNo)) {
                        final IdoShutsugakuInformationDTO information = (IdoShutsugakuInformationDTO) idoShutsugakuInformationMap
                                .get(kanriNo);

// ▽▽ 2009.10.05 h.matsuda 障害対応UPEX-1126 del start	                   
//                        jogaiTaisho = jogaiTaishoIdoKubunList
//                                .contains(information.getShubetsuKubun());
// △△ 2009.10.05 h.matsuda 障害対応UPEX-1126 del end 
                        
                        // 処理機能記述:2-2で取得した異動区分が、2-1の異動区分リストに存在しない場合
                        if (!jogaiTaisho) {
                            // 備考（ないしはメッセージ）を設定
                            biko = information.getShubetsuName();
                        }
                    }
                }

                // 除外対象でなければリストへ追加
                if (!jogaiTaisho) {
                    final Kmc00202AL01Bean bean = makeKmc00202AL01Bean(
                            seisekiService, saitenService, pagePc, pc, dto,
                            rowClass);
                    // 備考（ないしはメッセージ）の設定
                    bean.setBiko(biko);

                    rowList.add(bean);
                }
            }

            // 個人情報照会のリンク可否設定
            GakuseiInfoLinkChecker checker = new GakuseiInfoLinkChecker();
            String jinjCd = UtilUpSystem.getUpSystemData().getLoginUserBean()
                    .getJinjiCd();
            checker.setLinkInfo(rowList, jinjCd, getDbs());

            // 一覧に空行ができる場合 空行にスタイルを設定
            final int rowCnt = getRow(pc);
            if (listData.size() % rowCnt > 0) {
                for (int i = 0; i < rowCnt - (listData.size() % rowCnt); i++) {
                    rowClass.append(",rowClass1");
                }
            }

            pc.getPropSaitenTorokuTable().getColumnClasses();

            //サービスから、一覧データ取得する
            pc.getPropSaitenTorokuTable().setList(rowList);

            //一覧の件数を取得する
            pc.getPropSaitenTorokuTable().setRows(rowCnt);
            final String rowClasses = rowClass.substring(1);
            pc.getPropSaitenTorokuTable().setRowClasses(rowClasses);
            pc.getPropSaitenTorokuTable().setStockRowClasses(rowClasses);
            pc.getPropSaitenTorokuTable().setRendered(true);

            
            pc.setSyussekiFlag(getAcquireShukketsuKanriBoolean(pagePc, pc));
            pc.setSemesterFlag(semesterDsp);
            if (semesterDsp) {
                //出席率あるの場合
                if (pc.isSyussekiFlag()) {
                    pc.getPropSyussekirituCol().setRendered(true);
                    pc.getPropSemesterCol().setRendered(true);
                    pc.getPropSaitenTorokuTable().setColumnClasses(
                            "listGakuseki center," + "listShimei,"
                            + "listSoten center," + "listHyoka center,"
                            + "listSyusseki right,"
                    		+ "listGakunen center," + "listSemester center,"
							+ "listShozokGakka,"
                            + "listMessage");
                }
                //出席率ないの場合
                else {
                    pc.getPropSyussekirituCol().setRendered(false);
                    pc.getPropSemesterCol().setRendered(true);
                    pc.getPropSaitenTorokuTable().setColumnClasses(
                            "listGakuseki center," + "listShimei,"
                            + "listSoten center," + "listHyoka center,"
                    		+ "listGakunen center," + "listSemester center,"
							+ "listShozokGakka,"
                            + "listMessage");
                }
            } else {
                //出席率あるの場合
                if (pc.isSyussekiFlag()) {
                    pc.getPropSyussekirituCol().setRendered(true);
                    pc.getPropSemesterCol().setRendered(false);
                    pc.getPropSaitenTorokuTable().setColumnClasses(
                            "listGakuseki center," + "listShimei,"
                            + "listSoten center," + "listHyoka center,"
                            + "listSyusseki right,"
                            + "listGakunen center," + "listShozokGakka,"
                            + "listMessage");
                }
                //出席率ないの場合
                else {
                    pc.getPropSyussekirituCol().setRendered(false);
                    pc.getPropSemesterCol().setRendered(false);
                    pc.getPropSaitenTorokuTable().setColumnClasses(
                            "listGakuseki center," + "listShimei,"
                            + "listSoten center," + "listHyoka center,"
                            + "listGakunen center," + "listShozokGakka,"
                            + "listMessage");
                }
            }
        } else {
            pc.getPropSaitenTorokuTable().setRendered(false);
        }
    }

    /**
     * サービスから、一覧データ取得します。 <br>
     * 
     * @param pagePc
     *            Kmc00201Aページコード
     * @param pc
     *            Kmc00202Aページコード
     * @return 一覧DTOのリスト
     */
    private List getJyugyoRisyu(Kmc00201A pagePc, Kmc00202A pc) {
        final JugyoService jugyoService = new JugyoService(this);

        //検索条件をセット
        final JugyoRishuCondition condition = new JugyoRishuCondition();
        condition.setNendo(Integer.valueOf(pagePc.getPropNendo()
                .getStringValue()));
        condition.setGakkiNo(Integer.valueOf(pagePc.getPropGakkiNo()
                .getStringValue()));
        condition.setJugyoCd(pc.getJugyoCd());
        condition.setShikenKbn(new Integer(pagePc.getShikenFlg()));
        //試験区分は定期試験がない
        if (pagePc.getShikenFlg() != ShikenUPKbn.TEIKISHIKEN.getCode()) {
            condition.setSikenKaisu(Integer.valueOf(pc.getShikenKaisu()));
        }
        condition.setNarabijun(pc.getPropRow().getStringValue());
        condition.setHyoji(pc.getPropDisplay().getStringValue());
        //教員コードをセットします。
        // 6/26修正St↓↓↓↓↓↓↓↓
        condition.setJinjiCd(pagePc.getKyoinCd());
        // 6/26修正En↑↑↑↑↑↑↑↑

        //読み仮名区分を設定
        condition.setYomiganaKbn(pc.getYomiganaKbn());
        
// 2008-02-08 UPEX-250 START
        //学科表示区分を設定
        condition.setDspGakka(pc.getDspGakka());
// 2008-02-08 UPEX-250 END        

        try {
            return jugyoService.listRisyu(condition);
        } catch (NoSuchDataException e) {
            UtilSystem.getDisplayInfo().setDisplayMessage(
                    (UtilUpMsg.editMsg(UtilProperty
                            .getMsgString(SyMsgConst.SY_MSG_0042E))));

            return new ArrayList(0);
        }
    }

    /**
     * 指定した条件の出席率を取得します。 <br>
     * 
     * @param pagePc
     *            Kmc00201Aページコード
     * @param pc
     *            Kmc00202Aページコード
     * @param jugyoRisyuDTO
     *            年度学期ＤＴＯクラス
     * @return 出席率
     */
    private String getAcquirShussekiRitsu(SeisekiService seisekiService,
            Kmc00201A pagePc, Kmc00202A pc, JugyoRisyuDTO jugyoRisyuDTO) {
        //検索条件をセット
        ShussekiRitsuCondition shussekiRitsuCondition = new ShussekiRitsuCondition();
        shussekiRitsuCondition.setNendo(Integer.parseInt(pagePc.getPropNendo()
                .getStringValue()));
        shussekiRitsuCondition.setGakkiNo(Integer.parseInt(pagePc
                .getPropGakkiNo().getStringValue()));
        // 6/21 バグ修正の為コメント化↓↓↓↓↓
        //		shussekiRitsuCondition.setJugyoCd(
        //				pc.getPropKamokumei().getStringValue());
        // 6/21 バグ修正の為コメント化↑↑↑↑↑
        // 6/21 バグ修正の為追記↓↓↓↓↓
        shussekiRitsuCondition.setJugyoCd(pc.getJugyoCd());
        // 6/21 バグ修正の為追記↑↑↑↑↑

        shussekiRitsuCondition.setKanriNo(jugyoRisyuDTO.getKanriBangou()
                .longValue());
        // 基準日にシステムの現在日付を設定
        // 「4："yyyy-M-d"」を指定
        shussekiRitsuCondition.setKijunbi(DateFactory.getInstance());
        //出席率を取得
        ShussekiRitsuVO vo = seisekiService
                .acquireShussekiRitsu(shussekiRitsuCondition);

        String shussekiRitsu;
        if (vo == null) {
            shussekiRitsu = null;
        } else {
            shussekiRitsu = vo.getValue().toString();
        }
        return shussekiRitsu;
    }

    /**
     * 指定された授業に出欠率が設定されているか判定します。 <br>
     * 
     * @param pagePc
     *            Kmc00201Aページコード
     * @param pc
     *            Kmc00202Aページコード
     * @return 出欠率のフラグ
     */
    private boolean getAcquireShukketsuKanriBoolean(Kmc00201A pagePc,
            Kmc00202A pc) {

        JugyoService jugyoService = new JugyoService(this);
        //検索条件をセット
        ShukketsuSetteiCondition shukketsuSetteiCondition = new ShukketsuSetteiCondition();
        shukketsuSetteiCondition.setKaikoNendo(Integer.valueOf(pagePc
                .getPropNendo().getStringValue()));
        shukketsuSetteiCondition.setGakkiNo(Integer.valueOf(pagePc
                .getPropGakkiNo().getStringValue()));
        shukketsuSetteiCondition.setJugyoCode(pc.getJugyoCd());
        boolean flag = false;
        try {
            //出欠率flagを取得
            flag = jugyoService
                    .acquireShukketsuKanriBoolean(shukketsuSetteiCondition);
        } catch (NoSuchDataException e) {
            UtilLog.error(this.getClass(), e);
            throw new GakuenSystemException(e);
        }

        return flag;
    }

    /**
     * 授業ごとの学生の素点を取得します。 <br>
     * 
     * @param pagePc
     *            Kmc00201Aページコード
     * @param pc
     *            Kmc00202Aページコード
     * @param jugyoRisyuDTO
     *            年度学期ＤＴＯクラス
     * @return 素点のString
     */
    private String getSoten(SaitenService saitenService, Kmc00201A pagePc,
            Kmc00202A pc, JugyoRisyuDTO jugyoRisyuDTO) {

        String soten = null;
        //検索条件をセット
        HyokaCondition condition = new HyokaCondition();
        condition.setKaikoNendo(Integer.valueOf(pagePc.getPropNendo()
                .getStringValue()));
        condition.setGakkiNo(Integer.valueOf(pagePc.getPropGakkiNo()
                .getStringValue()));
        condition.setJugyoCd(pc.getJugyoCd());
        condition.setKanriNo(jugyoRisyuDTO.getKanriBangou());
        condition.setShikenKbn(String.valueOf(pagePc.getShikenFlg()));
        //試験区分は定期試験
        if (pagePc.getShikenFlg() == ShikenUPKbn.TEIKISHIKEN.getCode()) {
            try {
                soten = saitenService.acquireHyokaSotenTeiki(condition);
            } catch (NoSuchDataException e) {
                UtilSystem.getDisplayInfo().setDisplayMessage(
                        UtilUpMsg.editMsg(UtilProperty
                                .getMsgString(SyMsgConst.SY_MSG_0042E)));
            }
        } else {
            try {
                condition.setSikenKaisu(Integer.valueOf(pc.getShikenKaisu()));
                soten = saitenService.acquireHyokaSotenTsuisaishi(condition);
            } catch (NoSuchDataException e) {
                UtilSystem.getDisplayInfo().setDisplayMessage(
                        UtilUpMsg.editMsg(UtilProperty
                                .getMsgString(SyMsgConst.SY_MSG_0042E)));
            }
        }

        return soten;
    }

    /**
     * 学生の異動状況を取得します。 <br>
     * 
     * @param pagePc
     *            Kmc00201Aページコード
     * @param jugyoRisyuDTO
     *            年度学期ＤＴＯクラス
     * @return 異動区分名称
     */
    private String getAcquireGakuseiIdoState(GakuseiService gakuseiService,
            Kmc00201A pagePc, JugyoRisyuDTO jugyoRisyuDTO) {
        //検索条件をセット
        GakuseiIdoCondition gakuseiIdoCondition = new GakuseiIdoCondition();
        gakuseiIdoCondition.setKanriNo(jugyoRisyuDTO.getKanriBangou());
        gakuseiIdoCondition.setKaikoNendo(Integer.valueOf(pagePc.getPropNendo()
                .getStringValue()));
        gakuseiIdoCondition.setKaikoGakkiNo(Integer.valueOf(pagePc
                .getPropGakkiNo().getStringValue()));
        //定期試験
        if (pagePc.getShikenFlg() == ShikenUPKbn.TEIKISHIKEN.getCode()) {
            // 6/25修正↓↓↓↓↓↓↓
            //			gakuseiIdoCondition.setShikenKbn("1");
            gakuseiIdoCondition.setShikenKbn(String
                    .valueOf(ShikenUPKbn.TEIKISHIKEN.getCode()));
            // 6/25修正↑↑↑↑↑↑
            //追試験
        } else if (pagePc.getShikenFlg() == ShikenUPKbn.TSUISHIKEN.getCode()) {
            // 6/25修正↓↓↓↓↓↓↓
            //			gakuseiIdoCondition.setShikenKbn("2");
            gakuseiIdoCondition.setShikenKbn(String
                    .valueOf(ShikenUPKbn.TSUISHIKEN.getCode()));
            // 6/25修正↑↑↑↑↑↑
            //再試験
        } else {
            // 6/25修正↓↓↓↓↓↓↓
            //			gakuseiIdoCondition.setShikenKbn("3");
            gakuseiIdoCondition.setShikenKbn(String
                    .valueOf(ShikenUPKbn.SAISHIKEN.getCode()));
            // 6/25修正↑↑↑↑↑↑
        }
        String biko = null;
        try {
            biko = gakuseiService.acquireGakuseiIdoState(gakuseiIdoCondition);
        } catch (NoSuchDataException e) {
            return biko;
        }
        return biko;
    }

    /**
     * 一覧の件数を取得します。 <br>
     * 
     * @param pc
     *            Kmc00202Aのページコード
     * @return 一覧の件数
     */
    private int getRow(Kmc00202A pc) {

        RowCountCondition condition = new RowCountCondition();
        // 分類（これは固定）
        // パラメータ一斉置換 2006.06.24 Horiguchi Start
        //		condition.setBunrui("ROW_COUNT");
        condition.setBunrui("KMC");
        // パラメータ一斉置換 2006.06.24 Horiguchi End

        // 画面ID
        condition.setKoumoku(pc.getFormId().toUpperCase());
        // パラメータ一斉置換 2006.06.24 Horiguchi Start
        //		condition.setKoumoku(pc.getFormId().toUpperCase());
        condition.setKoumoku("KMC00202A2");
        // パラメータ一斉置換 2006.06.24 Horiguchi End
        // 枝番
        // パラメータ一斉置換 2006.06.24 Horiguchi Start
        //		condition.setSeqNo(new Integer("2"));
        condition.setSeqNo(new Integer("0"));
        // パラメータ一斉置換 2006.06.24 Horiguchi End

        // 設定値サービスから一覧の件数を取得して、表示行数設定を実施する
        SettingValueService setVal = new SettingValueService(this);
        int rowCnt = 0;
        try {
            rowCnt = setVal.acquireTableRowCount(condition);
        } catch (NoSuchDataException notE) {
            // 該当データが存在しない場合は、初期値「0」を設定する。
            rowCnt = 0;
        }
        return rowCnt;
    }

    /**
     * オプションテーブルに検索条件を保持します。 <br>
     * 
     * @param pc
     *            Kmc00202Aのページコード
     */
      protected void saveDefaultItemValue(Kmc00202A pc) {
        String loginId = UtilUpSystem.getUpSystemData().getLoginUserBean()
                .getLoginId();
        UtilCosOpt utilOpt = new UtilCosOpt(getDbs(), loginId, pc.getFormId());
        utilOpt.preLoad(); // データを先読みしてDAOのレコードキャッシュに格納
        utilOpt.setValue(pc.getPropRow().getId(), pc.getPropRow()
                .getStringValue());
        utilOpt.setValue(pc.getPropDisplay().getId(), pc.getPropDisplay()
                .getStringValue());
        utilOpt.setValue(pc.getPropHyokaWariai().getId(), pc.getPropHyokaWariai()
                .getStringValue());
		utilOpt.setValue(pc.getPropHyokaWariaiChk().getId(), 
				String.valueOf(pc.getPropHyokaWariaiChk().isChecked()));
        utilOpt.setValue(pc.getPropHyokaWariaiPrt().getId(), pc.getPropHyokaWariaiPrt()
                .getStringValue());
    }

    /**
     * コンボボックスを初期化します。 <br>
     * 
     * @param pc
     *            Kmc00202Aのページコード
     */
    private void narabijunBind(Kmc00202A pc) {
        pc.getPropRow().getList().clear();
        pc.getPropDisplay().getList().clear();
        // 全員表示
        pc.getPropDisplay().addListItem("1",
                UtilProperty.getMsgItemString("KM_AllDisplay"));
        // 最高学年の学生のみ
        pc.getPropDisplay().addListItem("2",
                UtilProperty.getMsgItemString("KM_HighestNomiDisplay"));
        // 最高学年の学生以外
        pc.getPropDisplay().addListItem("3",
                UtilProperty.getMsgItemString("KM_HighestIgaiDisplay"));
        pc.getPropDisplay().setStringValue("1");
        // 学籍番号順
        pc.getPropRow().addListItem("1",
                UtilProperty.getMsgItemString("KM_GakusekiCdSort"));

        String yomiganaKbn = acquireYomiGanaKbn();
        if (yomiganaKbn.equals(YomiganaKbn.KANASHIMEI.getCode())) {
            // カナ氏名順
            pc.getPropRow().addListItem("2",
                    UtilProperty.getMsgItemString("KM_KanaSimeiSort"));
        } else if (yomiganaKbn.equals(YomiganaKbn.EIGOSHIMEI.getCode())) {
            // 英語氏名順
            pc.getPropRow().addListItem("2",
                    UtilProperty.getMsgItemString("KM_EnglishSimeiSort"));
        }
        pc.setYomiganaKbn(yomiganaKbn);

        // 学科組織、学年（大きい順）
        pc.getPropRow().addListItem("3",
                UtilProperty.getMsgItemString("KM_GakkaDesc"));
        // 学科組織、学年（小さい順）
        pc.getPropRow().addListItem("4",
                UtilProperty.getMsgItemString("KM_GakkaAsc"));
        // 学年（大きい順）
        pc.getPropRow().addListItem("5",
                UtilProperty.getMsgItemString("KM_GakunenDesc"));
        // 学年（小さい順）
        pc.getPropRow().addListItem("6",
                UtilProperty.getMsgItemString("KM_GakunenAsc"));
        String loginId = UtilUpSystem.getUpSystemData().getLoginUserBean()
                .getLoginId();
        
        // 学年（小さい順）、みなし入学年度学期（大きい順）、学籍番号順（小さい順）
        pc.getPropRow().addListItem("7",
                UtilProperty.getMsgItemString("KM_GakunenNendoGakkiGakusekiCdSort"));
        if (semesterDsp) {
        	// 学科組織、セメスタ（大きい順）
	        pc.getPropRow().addListItem("8",
	                UtilProperty.getMsgItemString("KM_GakkaSemesterDescSort"));
	        // 学科組織、セメスタ（小さい順）
	        pc.getPropRow().addListItem("9",
	                UtilProperty.getMsgItemString("KM_GakkaSemesterAscSort"));
	        // セメスタ（大きい順）
	        pc.getPropRow().addListItem("10",
	                UtilProperty.getMsgItemString("KM_SemesterDescSort"));
	        // セメスタ（小さい順）
	        pc.getPropRow().addListItem("11",
	                UtilProperty.getMsgItemString("KM_SemesterAscSort"));
	        // セメスタ（小さい順）、みなし入学年度学期（大きい順）、学籍番号順（小さい順）
	        pc.getPropRow().addListItem("12",
	                UtilProperty.getMsgItemString("KM_SemesterNendoGakkiGakusekiCdSort"));
        }
        
        // 評価割合
        pc.getPropHyokaWariai().getList().clear();
        // 評価コード毎
        pc.getPropHyokaWariai().addListItem("1",
                UtilProperty.getMsgItemString("KM_HyokaCdJoken"));
        // 評価基準、評価コード毎
        pc.getPropHyokaWariai().addListItem("2",
                UtilProperty.getMsgItemString("KM_HyokaKijunHyokaCdJoken"));

        // 評価割合出力チェック
        pc.getPropHyokaWariaiChk().setChecked(false);
        
        // 評価割合印刷
        pc.getPropHyokaWariaiPrt().getList().clear();
        // 評価コード毎
        pc.getPropHyokaWariaiPrt().addListItem("1",
                UtilProperty.getMsgItemString("KM_HyokaCdJoken"));
        // 評価基準、評価コード毎
        pc.getPropHyokaWariaiPrt().addListItem("2",
                UtilProperty.getMsgItemString("KM_HyokaKijunHyokaCdJoken"));
        
        UtilCosOpt utilOpt = new UtilCosOpt(getDbs(), loginId, pc.getFormId());
        utilOpt.preLoad(); // データを先読みしてDAOのレコードキャッシュに格納
        // ここでは年度を設定しています。
        String row = utilOpt.getValue(pc.getPropRow().getId());
        if (row != null) {
            pc.getPropRow().setStringValue(row);
        } else {
            pc.getPropRow().setStringValue("1");
        }
        String display = utilOpt.getValue(pc.getPropDisplay().getId());
        if (display != null) {
            pc.getPropDisplay().setStringValue(display);
        } else {
            pc.getPropDisplay().setStringValue("1");
        }
        String hyoka = utilOpt.getValue(pc.getPropHyokaWariai().getId());
        if (hyoka != null) {
            pc.getPropHyokaWariai().setStringValue(hyoka);
        } else {
            pc.getPropHyokaWariai().setStringValue("1");
        }
        
		String HyokaWariaiChk = 
			utilOpt.getValue(pc.getPropHyokaWariaiChk().getId());
		if (HyokaWariaiChk != null) {	
			if (HyokaWariaiChk.equals(String.valueOf(true))) {
				pc.getPropHyokaWariaiChk().setChecked(true);
			} else {
				pc.getPropHyokaWariaiChk().setChecked(false);
			}
		}
		
        String hyokaPrt = utilOpt.getValue(pc.getPropHyokaWariaiPrt().getId());
        if (hyokaPrt != null) {
            pc.getPropHyokaWariaiPrt().setStringValue(hyokaPrt);
        } else {
            pc.getPropHyokaWariaiPrt().setStringValue("1");
        }
    }

    /**
     * @return 読み仮名区分
     */
    private String acquireYomiGanaKbn() {

        String yomiganaKbn = null;
        // 教員付加情報を取得
        // 人事DAO
        CoiJinjDAO jnjDAO = (CoiJinjDAO) getDbs().getDao(CoiJinjDAO.class);
        CoiJinjAR jinjAR = null;
        try {
            SearchUserBean searchUserBean = UtilUpSystem.getUpSearchData()
                    .getSearchUserBean();
            if (searchUserBean != null) {
                String kyoinCode = searchUserBean.getKanriJinjiCd();
                jinjAR = jnjDAO.findByPrimaryKey(kyoinCode);
            }
            if (jinjAR != null) {
                yomiganaKbn = jinjAR.getYomiganaKbn();
            }

        } catch (DbException e) {
            yomiganaKbn = null;
        }
        return yomiganaKbn;
    }

// 2008-02-08 UPEX-250 START
    /**
     * 学科表示区分を取得する
     * <P>
     * パラメータより学科表示区分を取得します（0:所属学科 1:カリキュラム学科）
     * 
     * @param pc Kmc00202A
     * @return トランザクション処理の結果
     */
    private void setDspGakka(Kmc00202A pc){
		// パラメータDAO
		CouParamDAO paramDAO = 
			(CouParamDAO) getDbs().getDao(CouParamDAO.class);		
		// 表示する学科を取得
		CouParamAR dspGakka = null;
		try {
			dspGakka = paramDAO.findByPrimaryKey("KMC","GAKKA_NAME",0);
			
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		}
		// ページコードへ設定
		if (dspGakka != null) {
			pc.setDspGakka(dspGakka.getValue());
		} else {
			// 所属学科組織をデフォルトとする。
			pc.setDspGakka("0");
		}
    }
// 2008-02-08 UPEX-250 END    

    /**
     * 総計グラフ表示区分を取得する。
     * パラメータより総計グラフ表示区分を取得します（0:表示しない 1:表示する）
     * 
     * @param pc Kmc00202A
     */
    private void setSokeiGraphFlag(Kmc00202A pc){
		// パラメータDAO
		CouParamDAO paramDAO = (CouParamDAO) getDbs().getDao(CouParamDAO.class);		
		// 総計グラフ表示区分を取得
		CouParamAR couParamAR = null;
		try {
		    couParamAR = paramDAO.findByPrimaryKey("KMC","DSP_SOKEI",0);
			
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		}
		// ページコードへ設定
		if (couParamAR != null && "0".equals(couParamAR.getValue())) {
			pc.setSokeiGraphFlg(false);					// 総計グラフ非表示
			pc.getPropHyokaWariai().setValue("2");		// 評価割合＝「評価基準、評価コード毎」
			pc.getPropHyokaWariaiPrt().setValue("2");
		} else {
			pc.setSokeiGraphFlg(true);
		}
    }

    /**
     * 登録処理を行います。
     * <P>
     * 登録、更新画面より一覧画面に戻します。
     * 
     * @param pagecode
     *            ページコード
     * @return トランザクション処理の結果
     */
    protected String update(PageCodeBaseEx pagecode) {
        final Kmc00202A pc = (Kmc00202A) pagecode;
        Kmc00201A pagePc = (Kmc00201A) UtilSystem
                .getManagedBean(Kmc00201A.class);

        // 画面の隠し項目（年度、学期、授業コード、試験区分、試験回数の結合文字列）を取得
        String browserHidden = pc.getPropBrowserHidden().getStringValue();
        // サーバの項目（年度、学期、授業コード、試験区分、試験回数の結合文字列）を取得
        String pcItem = pc.getPropNendo().getStringValue() + "|" +  pc.getPropGakki().getStringValue() 
							+ "|" +  pc.getJugyoCd() + "|" +  pc.getPropShikenKubun().getStringValue() 
							+ "|" +  UtilStr.cnvNull(pc.getShikenKaisu());
        // 複数画面の使用により、画面とサーバの内容が異なっていないかをチェック
        if (!pcItem.equals(browserHidden)) {
            UtilSystem.getDisplayInfo().setDisplayMessage(UtilUpMsg.editMsg(
            				UtilProperty.getMsgString(CoMsgConst.CO_MSG_0060E)));
            pc.setContentRendered(false);
            return UpActionConst.RET_FALSE;
        }
        
        // 確定ボタン押下時のページを表示
        pc.getPropSaitenTorokuTable().setDispPage(
        		pc.getPropSaitenTorokuTable().getRowNumber());
        
        if (pc instanceof Jgc91102A) {
       	 pagePc = (Kmc00201A) UtilSystem
            .getManagedBean(Jgc91101A.class);
        }
        final StringBuffer rowClass = new StringBuffer();
        final SaitenService saitenService = new SaitenService(this);
        final JugyoService jugyoService = new JugyoService(this);

        //空行対応
        final List dataList = pc.getPropSaitenTorokuTable().getNoEmptyList();

        // 評価略称再セット
        resethyokaName(pc);
		
        int rowCnt = getRow(pc);

        int errCount = 0;
        //		boolean updateFlg = false;

    	// 未採点者登録チェック
        if (misaitenChk.equals(CHK)) {
	        int chkFlg = pc.getPropExecutableFixed().getIntegerValue().intValue();
			
			if (chkFlg == 0) {
				boolean mitoroku = false; 

				final Iterator ite = dataList.iterator();
		        for (int i = 0; ite.hasNext(); i++) {
		            final Kmc00202AL01Bean listBean = (Kmc00202AL01Bean) ite.next();
		            if (UtilStr.cnvNull(listBean.getSoten()).equals("")) {
		            	mitoroku = true;
		            	break;
		            }
		        }				
					
				// 未採点者が存在する場合
				if (mitoroku) {
					UtilSystem.getDisplayInfo().setConfirmMessage(
							UtilProperty.getMsgString(
									KmMsgConst.KMC_MSG_0007W));
					return UpActionConst.RET_FALSE;
				}
			}
			pc.getPropExecutableFixed().setIntegerValue(new Integer(0));
        }

		final Iterator ite = dataList.iterator();
        //画面のデーターが取る
        for (int i = 0; ite.hasNext(); i++) {
            final Kmc00202AL01Bean listBean = (Kmc00202AL01Bean) ite.next();
            final HyokaKoshinValue hyokaKoshinValue = new HyokaKoshinValue();
            hyokaKoshinValue.setKaikoNendo(Integer.valueOf(pagePc
                    .getPropNendo().getStringValue()));
            hyokaKoshinValue.setGakkiNo(Integer.valueOf(pagePc.getPropGakkiNo()
                    .getStringValue()));
            hyokaKoshinValue.setJugyoCd(pc.getJugyoCd());
            hyokaKoshinValue.setKanriNo(listBean.getKanriNo());
            final String kyoinCode = UtilUpSystem.getUpSystemData()
                    .getLoginUserBean().getJinjiCd();
            hyokaKoshinValue.setSitnJinjiCd(kyoinCode);

            //定期試験以外試験回数セット
            if (pc.getShikenKaisu() != null) {
                hyokaKoshinValue.setSikenKaisu(Integer.valueOf(pc
                        .getShikenKaisu()));
            }
            hyokaKoshinValue.setSitnUpdateDate(DateFactory.getInstance());

            //素点運用の場合
            hyokaKoshinValue.setSaitenKbn(String.valueOf(SaitenKbn.SOTEN
                    .getCode()));

            try {
                // チェックのメッセージ
                String message = "";

                //素点の入力チェックを行う
                message = acquireCheckMsg(jugyoService, listBean,
                        saitenService, pagePc);

                listBean.setMessage(message);

                //エラー数をカウント
                if (message.length() > 0) {
                    errCount++;
                }
                //		        if (listBean.getSoten() == null ||
                //						listBean.getSoten().trim().length() == 0) {
                //エラーメッセージがない、または素点未入力は背景白
                if (message.length() != 0
                        || (listBean.getSoten() == null
                        || UtilStr.cnvTrim(listBean.getSoten()).length() == 0)) {

                    rowClass.append(", rowClass1");
                } else {
                    rowClass.append(", selectiveLine");
                }

                //エラーでは無い場合は更新処理を行う
                if (message.length() == 0) {

                    Integer soten;
                    if (listBean.getSoten() == null
                            || UtilStr.cnvTrim(listBean.getSoten()).equals("")) {
                        soten = null;
                    } else {
                    	
                        // 評価で入力された場合
                        if (sotenAtukai.equals(HYOKA)
                         && UtilStr.chkHankakuNumberMoji(listBean.getSoten()) == false) {
                        	
                        	String sotenStr = cnvSoten(listBean.getNyugakNendoCur().intValue(),
            					            		 listBean.getNyugakGakkiNoCur().intValue(),
            										 listBean.getCurGakkaCd(),
            										 listBean.getSoten());
                        	if(!UtilStr.cnvNull(sotenStr).equals("")){
                        		if(sotenStr.length()>1){
                        			sotenStr = String.valueOf(sotenStr.substring(1, sotenStr.length()));
                            	}else{
                            		sotenStr = "0";
                            	}
                        	}
                            soten = new Integer(sotenStr);
                        } else {
                  	
                        soten = new Integer(listBean.getSoten());
                        
                        }
                        
                    }
                    hyokaKoshinValue.setHyokaTen(soten);
                    //				    updateFlg = true;
                    
                    if (pagePc.getShikenFlg() == ShikenUPKbn.TEIKISHIKEN
                            .getCode()) {
                        //定期試験の場合
                        saitenService.updateTeiki(hyokaKoshinValue);

                    } else {
                        //追再試験の場合
                        hyokaKoshinValue.setTuisaisikenKbn(String
                                .valueOf(pagePc.getShikenFlg()));
                        saitenService.updateTuisaishi(hyokaKoshinValue);
                    }
                }
            } catch (AlreadyUpdatePossibilityException e) {
                UtilSystem.getDisplayInfo().setDisplayMessage(
                        UtilUpMsg.editMsg(UtilProperty
                                .getMsgString(CoMsgConst.CO_MSG_0037E)));
                return UpActionConst.RET_FALSE;
            } catch (AlreadyUpdateException e) {
                UtilSystem.getDisplayInfo().setDisplayMessage(
                        UtilUpMsg.editMsg(UtilProperty
                                .getMsgString(CoMsgConst.CO_MSG_0037E)));
                return UpActionConst.RET_FALSE;
            } catch (NoSuchDataException e) {
                UtilSystem.getDisplayInfo().setDisplayMessage(
                        UtilUpMsg.editMsg((UtilProperty
                                .getMsgString(CoMsgConst.CO_MSG_0036E))));
                return UpActionConst.RET_FALSE;
            }
        }

        //		if (updateFlg == true) {
        if (errCount == 0) {
            // メッセージ「登録されました。」
            UtilSystem.getDisplayInfo().setPopupMessage(
                    UtilProperty.getMsgString(SyMsgConst.SY_MSG_0001I));
            pc.getPropValueChanged().setValue(Boolean.FALSE);
            pc.getPropHasInputError().setValue(Boolean.FALSE);
        }
        /// add 2006-07-02
        else {
            // メッセージ「エラーがありますので確認してください」
            UtilSystem.getDisplayInfo().setDisplayMessage(
                    UtilUpMsg.editMsg(UtilProperty
                            .getMsgString("KMC_MSG_0009E")));
            pc.getPropValueChanged().setValue(Boolean.TRUE);
            pc.getPropHasInputError().setValue(Boolean.TRUE);
        }
        /// add 2006-07-02 end

        // 一覧に空行ができる場合 空行にスタイルを設定
        if (dataList.size() % rowCnt > 0) {
            for (int i = 0; i < rowCnt - (dataList.size() % rowCnt); i++) {
                rowClass.append(",rowClass1");
            }
        }

        //採点登録済みの学生は、行をグレーにする。
        final String rowClasses = rowClass.substring(1);
        pc.getPropSaitenTorokuTable().setRowClasses(rowClasses);
        pc.getPropSaitenTorokuTable().setStockRowClasses(rowClasses);

        // スタイルの設定
        pc.getPropSaitenTorokuTable().editPageRowClass(
                pc.getPropSaitenTorokuTable().getRowNumber()
                        / pc.getPropSaitenTorokuTable().getRows());
        
// UPEX-1290　対応　2010/06/12 k.yokoi start
//  エラーが存在していても、正常登録データはコミット
//        //		return UpActionConst.RET_TRUE;
//        // add 2006-07-02
//        //エラーが1件でもある場合はコミットしない
//        if (errCount == 0) {
        //		return UpActionConst.RET_TRUE;
//        } else {
//            return UpActionConst.RET_FALSE;
//        }
//        // add 2006-07-02 end
            return UpActionConst.RET_TRUE;
// UPEX-1290　対応　2010/06/12 k.yokoi end
    }

    /**
     * 画面の評価略称に名称をセットします。
     * 
     * @param pagecode
     *            ページコード
     */
    protected void resethyokaName(PageCodeBaseEx pagecode) {

        final Kmc00202A pc = (Kmc00202A) pagecode;
        Kmc00201A pagePc = (Kmc00201A) UtilSystem
                .getManagedBean(Kmc00201A.class);
        if (pc instanceof Jgc91102A) {
       	 pagePc = (Kmc00201A) UtilSystem
            .getManagedBean(Jgc91101A.class);
        }
    	
        //空行対応
        List dataList = pc.getPropSaitenTorokuTable().getNoEmptyList();
        Kmc00202AL01Bean listBean = new Kmc00202AL01Bean();
        Kmc00202AL01Bean listBeanNew = new Kmc00202AL01Bean();

        String hyokaRyak = "";
        if (dataList != null && dataList.size() != 0) {
	        for (int i = 0; i < dataList.size(); i++) {
	            listBean = (Kmc00202AL01Bean)dataList.get(i);
	            listBeanNew = new Kmc00202AL01Bean();
	          
	            hyokaRyak = cnvHyokaRyakForReset(listBean.getNyugakNendoCur().intValue(),
	            			 listBean.getNyugakGakkiNoCur().intValue(),
							 listBean.getCurGakkaCd(),
							 listBean.getSoten());
	            
	            listBean.setHyokaMeisho(hyokaRyak);
	        }				
        }
    	
    }
    
    /**
     * @param jugyoService
     * @param listBean
     * @return
     */
    private String acquireCheckMsg(JugyoService jugyoService,
            Kmc00202AL01Bean listBean, SaitenService saitenService,
            Kmc00201A pagePc) {

        String message = "";
        if (listBean.getSoten() != null
            && UtilStr.cnvTrim(listBean.getSoten()).length() > 0) {
            String sotenHankakuString = UtilStr.cnvHankaku(listBean.getSoten());
            String sotenString = UtilStr.cnvTrim(sotenHankakuString);
            listBean.setSoten(sotenString);

// 2007/02/27 不具合管理一覧：No.3621 Start -->>
//            // エラーメッセージ表示用
//            int errMsgSotenToMax = 0;
//            //追試、再試以外
//            if (!(pagePc.getShikenFlg() == ShikenUPKbn.TEIKISHIKEN.getCode())) {
//                ShikenBetuHyokaKijunCondition condition = new ShikenBetuHyokaKijunCondition();
//                condition.setKanriNo(listBean.getKanriNo());
//                condition.setShikenKbn(String.valueOf(pagePc.getShikenFlg()));
//                try {
//                    errMsgSotenToMax = saitenService
//                            .acquireShikenBetuSotenMax(condition);
//                } catch (NoSuchDataException e) {
//                    //スルー
//                }
//            }
// <<-- End   2007/02/27 不具合管理一覧：No.3621

            // 画面入力素点退避用
            String sotenStringWk = sotenString;
            String soten = "";
            
            // 評価で入力された場合
            if (sotenAtukai.equals(HYOKA)) {
            	
            	if (UtilStr.chkHankakuNumberMoji(sotenString) == false) {

                	soten = cnvSoten(listBean.getNyugakNendoCur().intValue(),
		            		 listBean.getNyugakGakkiNoCur().intValue(),
							 listBean.getCurGakkaCd(),
							 sotenString);
                	if(!UtilStr.cnvNull(soten).equals("")){
                		if(soten.length()>1){
                    		soten = String.valueOf(soten.substring(1, soten.length()));
                    	}else{
                    		soten = "0";
                    	}
                	}
                	
            		
            		// 入力値が文字の場合
	            	if (UtilStr.cnvNull(soten).equals("")
	            	 || Integer.valueOf(soten).intValue() <= 100) {
	            		// 評価で入力された場合、評価基準に存在しない評価コード又は
	            		// 素点範囲が100点以下に該当する評価コードの場合はエラー
	                    message = UtilProperty.getMsgString(KmMsgConst.KMC_MSG_0007E);
	                    return message;
	            	} else {
	            		sotenString = soten;
	                    listBean.setSoten(sotenString);
	            	}
            	} else {
            		// 入力値が数値の場合
	            	if (!UtilStr.cnvNull(sotenString).equals("")
   	            	 && Integer.valueOf(sotenString).intValue() >= 101) {
   	            		// 数値で入力された場合、評価基準に存在しない評価コード又は
   	            		// 素点範囲が101点以上に該当する評価コードの場合はエラー
	                    message = UtilProperty.getMsgString(KmMsgConst.KMC_MSG_0025E);
	                    return message;
	            	} else {
	                    listBean.setSoten(sotenString);
	            	}
            	}
            }
            
            if (UtilStr.chkHankakuNumberMoji(sotenString) == false) {
                //入力素点は数字がない
                message = UtilProperty.getMsgString(SyMsgConst.SY_MSG_0160E,
                        SaitenKbn.SOTEN.getName());
                return message;
            }

            //		    if (Integer.parseInt(sotenString) < 0 ||
            //					Integer.parseInt(sotenString) > 100) {
            if (Integer.parseInt(sotenString) < 0) {
                //素点に数値の範囲を0～100にしない
                //				String[] errmessage = new String[3];
                String[] errmessage = new String[2];
                errmessage[0] = SaitenKbn.SOTEN.getName();
                errmessage[1] = "0";
                message = UtilProperty.getMsgString(SyMsgConst.SY_MSG_0051E,
                        errmessage);
                //				errmessage[2] = "100";
                //				message = UtilProperty.getMsgString(
                //						SyMsgConst.SY_MSG_0021E, errmessage);
                return message;
            }

// 2007/02/27 不具合管理一覧：No.3621 Start -->>
//            // 6/29修正St↓↓↓↓↓↓↓↓
//            //追再試験時のみチェック
//            if (!checkMaxSoten(saitenService, listBean.getKanriNo(), pagePc
//                    .getShikenFlg(), listBean.getSoten())) {
//                //素点の最大値を超えています（最大値：{0}）。
//                message = UtilProperty.getMsgString("KMC_MSG_0008E", String
//                        .valueOf(errMsgSotenToMax));
//                return message;
//            }
//            // 6/29修正En↑↑↑↑↑↑↑↑
// <<-- End   2007/02/27 不具合管理一覧：No.3621
            // 6/23修正St↓↓↓↓↓↓↓↓
            // 定期試験時のみ
            if (!checkHyokaSoten(jugyoService, listBean.getNyugakNendoCur(),
                    listBean.getNyugakGakkiNoCur(), listBean
                            .getCurGakkaCd(), listBean.getSoten())) {

                message = UtilProperty.getMsgString(KmMsgConst.KMC_MSG_0006E);
                return message;
            }
            // 6/23修正En↑↑↑↑↑↑

// 2007/02/27 不具合管理一覧：No.3621 Start -->>
			try {
	            //追再試験時のみチェック
				String err = saitenService.getCheckMaxSotenErrMsg(saitenService.checkMaxSoten(	listBean.getKanriNo().longValue(),
	            																				pagePc.getShikenFlg(),
																								Integer.parseInt(listBean.getSoten())));
				if(err != null) {
					message = err;
	                return message;
	            }
			} catch (DbException e) {
				e.printStackTrace();
				throw new GakuenSystemException(e);
			} catch (NoSuchDataException e) {
				e.printStackTrace();
				throw new GakuenSystemException(e);
			}
// <<-- End   2007/02/27 不具合管理一覧：No.3621

            // 画面入力素点退避用を戻す
            listBean.setSoten(sotenStringWk);
        }
        return message;
    }

    // 6/23修正St↓↓↓↓↓↓↓↓
    /**
     * 
     * エラー判定（素点範囲チェック）
     * 
     * @param service
     *            授業サービス
     * @param nnc
     *            入学年度
     * @param ngnc
     *            入学学期No
     * @param sg
     *            素点
     * @param st
     *            カリキュラム学科組織コード
     * @return service.checkHyokaSoten()戻り値
     */
    private boolean checkHyokaSoten(JugyoService service, Integer nnc,
            Integer ngnc, String sg, String st) {

        SotenRangeCondition condition = new SotenRangeCondition();
        condition.setNendo(nnc);
        condition.setGakkiNo(ngnc);
        condition.setCurGakkaCd(sg);
        condition.setSoten(Integer.valueOf(st));
        condition.setSoten(Integer.valueOf(st));
        return service.checkHyokaSoten(condition);
    }

    // 6/23修正En↑↑↑↑↑↑↑↑

    /**
     * @param kmc00201A
     * @param kmc00202A
     * @return
     */
    private JogaiTaishoIdoKubunCondition makeJogaiTaishoIdoKubunCondition(
            Kmc00201A kmc00201A, Kmc00202A kmc00202A) {
        final JogaiTaishoIdoKubunCondition condition = new JogaiTaishoIdoKubunCondition();

        // 授業コード
        condition.setJugyoCode(kmc00202A.getJugyoCd());

        // 開講年度
        String str = kmc00201A.getPropNendo().getStringValue();
        if (str != null) {
            condition.setKaikoNendo(Integer.parseInt(str));
        } else {
            UtilLog.warn(getClass(), "開講年度が設定されていません。");
        }

        // 学期NO
        str = kmc00201A.getPropGakkiNo().getStringValue();
        if (str != null) {
            condition.setGakkiNo(Integer.parseInt(str));
        } else {
            UtilLog.warn(getClass(), "学期NOが設定されていません。");
        }

        // 試験区分
        condition.setShikenKubun(String.valueOf(kmc00201A.getShikenFlg()));

        return condition;
    }

    /**
     * @param gakuseiService
     * @return
     */
    private Map searchIdoShutsugakuInformations(GakuseiService gakuseiService) {
        final Map map = new HashMap();

        final Iterator idoShutsugakuInformations = gakuseiService
                .listIdoShutsugakuInformation(DateFactory.getInstance())
                .iterator();
        while (idoShutsugakuInformations.hasNext()) {
            final IdoShutsugakuInformationDTO idoShutsugakuInformation = (IdoShutsugakuInformationDTO) idoShutsugakuInformations
                    .next();

            map.put(new Long(idoShutsugakuInformation.getKanriNo()),
                    idoShutsugakuInformation);
        }

        return map;
    }

    /**
     * @param seisekiService
     * @param saitenService
     * @param kmc00201A
     * @param kmc00202A
     * @param dto
     * @param sb
     * @return
     */
    private Kmc00202AL01Bean makeKmc00202AL01Bean(
            SeisekiService seisekiService, SaitenService saitenService,
            Kmc00201A kmc00201A, Kmc00202A kmc00202A, JugyoRisyuDTO dto,
            StringBuffer sb) {
        final Kmc00202AL01Bean bean = new Kmc00202AL01Bean();
        bean.setKanriNo(dto.getKanriBangou());
        bean.setGakuseki(dto.getGakuseki());
        bean.setHurigana(dto.getShimeiKana());
        bean.setShimei(dto.getShimeiKanji());
        if (dto.getGakunen() != null) {
            bean.setGakunen(dto.getGakunen().intValue());
        } else {
            bean.setGakunen(0);
        }
        
        if (semesterDsp) {
        	bean.setSemester(getSemester(dto.getKanriBangou().longValue()));
        }
        
// 2008-02-08 UPEX-250 START       
        if(kmc00202A.getDspGakka().equals("0")){
            bean.setShozokuGakka(dto.getShozokuGakka());        	
        } else if(kmc00202A.getDspGakka().equals("1")){
            bean.setShozokuGakka(dto.getCurGakka());        	
        }
// 2008-02-08 UPEX-250 END       
        
        // 6/23修正St↓↓↓↓↓↓↓↓
        bean.setShozokuGakkaCd(dto.getShozokuGakkaCd());
        bean.setCurGakkaCd(dto.getCurGakkaCd());
        bean.setNyugakNendoCur(dto.getNyugakNendoCur());
        bean.setNyugakGakkiNoCur(dto.getNyugakGakkiNoCur());
        // 6/23修正En↑↑↑↑↑↑↑↑

//障害対応 UPEX-153 採点登録画面の出欠情報を授業単位で表示する為修正 2009.02.23 Satomi Start        
//障害NO4082 出欠パフォーマンスアップ共通化 2007.08.20 Horiguchi Start
//        // 出席率の設定
//        if (getAcquireShukketsuKanriBoolean(kmc00201A, kmc00202A)) {
//            String shussekiRitsu = getAcquirShussekiRitsu(seisekiService,
//                    kmc00201A, kmc00202A, dto);
//            if (shussekiRitsu == null) {
//                bean.setSyussekiritu(Kmc00202B.NULL_NUM);
//                bean.setSyussekiRituNull(true);
//
//            } else {
//                bean.setSyussekiritu(shussekiRitsu + "%");
//                bean.setSyussekiRituNull(false);
//            }
//        }
        // 出席率の設定
//        if (ShukkessekiList != null) {
//            String shussekiRitsu = null;
//            ShukkessekiDTO sskDto = null;
//            for (int i = 0; i < ShukkessekiList.size(); i++) {
//                sskDto = (ShukkessekiDTO)ShukkessekiList.get(i);
//                if (sskDto.getKanriNo().equals(dto.getKanriBangou())) {
//                    shussekiRitsu = sskDto.getShussekiRitsu().toString();
//                    break;
//                }
//            }
//            if (shussekiRitsu == null) {
//                bean.setSyussekiritu(Kmc00202B.NULL_NUM);
//                bean.setSyussekiRituNull(true);
//
//            } else {
//                bean.setSyussekiritu(shussekiRitsu + "%");
//                bean.setSyussekiRituNull(false);
//            }

        // 出席率の設定
        if( SyuYaKuMap != null && !SyuYaKuMap.isEmpty() ){
            
        	String shussekiRitsu = null;
            ShukkessekiDTO sskDto = null;
            
            Long kanriNo = dto.getKanriBangou();
            
            // 集約マップのKeyに対象の管理番号が存在するかチェック
            if( SyuYaKuMap.containsKey( kanriNo ) ){
            	
            	sskDto = (ShukkessekiDTO)SyuYaKuMap.get( kanriNo );
            	
            	float kessekiSu = Float.parseFloat( String.valueOf( sskDto.getKessekiKaisu()) );	// 欠席回数の取得
            	float jugyokaiSu = Float.parseFloat( String.valueOf( sskDto.getJugyoKaisu()) );		// 授業回数の取得
            	float syussekiSu = jugyokaiSu - kessekiSu;		// 出席回数の取得
            	
            	// 出席率の計算を四捨五入するように修正
            	if ( jugyokaiSu != 0 && syussekiSu != 0){
            		
            		BigDecimal shussekiPercent = 
            			new BigDecimal(String.valueOf((float)(syussekiSu * 100) / jugyokaiSu));
		    	
            		// 出席率を取得
            		shussekiRitsu = "" + shussekiPercent.setScale(0,BigDecimal.ROUND_HALF_UP);
            	}else{
	        		//出席率を[0]で設定
            		shussekiRitsu ="0";            		
            	}
            	
            }
            
            if (shussekiRitsu == null) {
                bean.setSyussekiritu(Kmc00202B.NULL_NUM);
                bean.setSyussekiRituNull(true);

            } else {
                bean.setSyussekiritu(shussekiRitsu + "%");
                bean.setSyussekiRituNull(false);
            }

        }
//障害NO4082 出欠パフォーマンスアップ共通化 2007.08.20 Horiguchi End
//障害対応 UPEX-153 採点登録画面の出欠情報を授業単位で表示する為修正 2009.02.23 Satomi End        
        bean.setSoten(getSoten(saitenService, kmc00201A, kmc00202A, dto));

        // チェックのメッセージ
        String message = "";
        boolean rowClassFlag = true;
        if (bean.getSoten() != null && bean.getSoten().length() > 0) {
            final String sotenString = UtilStr.cnvTrim(bean.getSoten());
            if(UtilStr.chkHankakuNumberMoji(sotenString) == false){  	
//            	入力素点は数字がない
            	String str= cnvSoten(bean.getNyugakNendoCur().intValue(),bean.getNyugakGakkiNoCur().intValue(),bean.getCurGakkaCd(),sotenString);
            	if ("".equals(str) || str.length()==1) {
            		message = UtilProperty.getMsgString(KmMsgConst.KMC_MSG_0007E,
	                        UtilProperty.getMsgItemString("KM_Soten"));
	            	 rowClassFlag = true;
            	} else {
            		rowClassFlag = false;
                }
            }
            else if (!"".equals(sotenString) && Integer.parseInt(sotenString) < 0) {

                //素点に数値の範囲を0以下にしない
                final String[] errmessage = new String[2];
                errmessage[0] = UtilProperty.getMsgItemString("KM_Soten");
                errmessage[1] = "0";
                message = UtilProperty.getMsgString(SyMsgConst.SY_MSG_0051E,
                        errmessage);
                //				final String[] errmessage = new String[3];
                //				errmessage[0] = UtilProperty.getMsgItemString("KM_Soten");
                //				errmessage[1] = "0";
                //				errmessage[2] = "100";
                //				message = UtilProperty.getMsgString(
                //						SyMsgConst.SY_MSG_0021E, errmessage);
            }else {
            		rowClassFlag = false;   
            }
        }

   		// 評価をセット
        bean.setHyokaMeisho(cnvHyokaRyak(dto.getNyugakNendoCur().intValue(),
				   dto.getNyugakGakkiNoCur().intValue(),
				   dto.getCurGakkaCd(),
				   bean.getSoten()));

        // 評価として登録でかつ101点以上の場合のみ評価点に該当する評価コードをセットする
   		bean.setSoten(cnvHyokaCd(dto.getNyugakNendoCur().intValue(),
								   dto.getNyugakGakkiNoCur().intValue(),
								   dto.getCurGakkaCd(),
								   bean.getSoten()));

        
        //異動区分リストに含まれる場合,当該異動区分の名称を表示する。
        if (bean.getBiko() == null) {
            bean.setMessage(message);
        } else {
            bean.setMessage("<br>" + message);
        }

        //採点登録済みの学生は、行をグレーにする。
        if (rowClassFlag) {
            sb.append(", rowClass1");
        } else {
            sb.append(", selectiveLine");
        }

        return bean;
    }
    
    /**
     * 管理番号よりセメスタを取得します
     * 
     * @param kainriNo
     * @return String
     */
    protected String getSemester(long kanriNo) {
    	
    	String ret = "";
    	
    	try {
    		// 現在日
    		java.sql.Date currentDate =
    			UtilDate.cnvSqlDate(
    					UtilDate.parseDate(UtilDate.getDateSystem()));

    		// 学籍
    		CobGaksekiDAO cobGaksekiDAO = (CobGaksekiDAO) getDbs()
    				.getDao(CobGaksekiDAO.class);
    		CobGaksekiAR cobGaksekiAR = null;

    		// 現在日にて最新の有効な学籍を取得する
    		cobGaksekiAR = cobGaksekiDAO.findCurrentByKanriNo(kanriNo, currentDate);
    		if (cobGaksekiAR != null) {
    			if (cobGaksekiAR.getSemester() != null) {
    				ret = String.valueOf(cobGaksekiAR.getSemester());
    			}
     		}
    		
		} catch (DbException e) {
			throw new RuntimeException(e);
		}	        
		return ret;
    }
    
    /**
     * パラメータの設定値が素点として登録時でかつ素点が101点以上の場合に素点を評価コードに変換します
     * 
     * @param nyugakNendoCur
     * @param nyugakGakkiNoCur
     * @param curGakkaCd
     * @param soten
     * @return
     */
    protected String cnvHyokaCd(int nyugakNendoCur,
    						   int nyugakGakkiNoCur,
							   String curGakkaCd,
							   String soten) {

    	String ret = "";
    	int intSoten = 0;

    	if (UtilStr.cnvNull(soten).equals("")) {
    		return ret;
    	}else if(UtilStr.chkHankakuNumberMoji(soten) == false){
    		//入力素点は数字がない
    		String str= cnvSoten(nyugakNendoCur,nyugakGakkiNoCur,curGakkaCd,soten);
    		//評価コードがありません
    		if("".equals(str)){
    			return soten;
    		}
    		//評価コードの素点は大于100
    		if(str.length()>1){
    			if(sotenAtukai.equals(HYOKA)){
    				return soten;
    			} else{
    				return str.substring(1,str.length());
    			}	
    		}
    		//普通の評価コード
    		return str;
    		
    	}else {
    		intSoten = Integer.valueOf(soten).intValue();
    	}
    	
		try {
			//入力素点は数字があります
			if (sotenAtukai.equals(SOTEN)) {
				return soten;
			} else if (sotenAtukai.equals(HYOKA)) {
				intSoten = Integer.valueOf(soten).intValue();
				if (intSoten < 101) {
					return soten;
				} else {
					// 評価基準配当
					KmbHykHaiUPDAO kmbHykHaiUPDAO = (KmbHykHaiUPDAO) getDbs().getDao(KmbHykHaiUPDAO.class);
					KmbHykHaiAR kmbHykHaiAR;

					// 評価基準
					KmzHykUPDAO kmzHykUPDAO = (KmzHykUPDAO) getDbs().getDao(KmzHykUPDAO.class);
					KmzHykAR kmzHykAR;

					kmbHykHaiAR = kmbHykHaiUPDAO.findByPrimaryKey(
							nyugakNendoCur,
							nyugakGakkiNoCur,
							curGakkaCd);
					
					if (kmbHykHaiAR != null) {
						List kmzHykARList = kmzHykUPDAO.findByHyokaKijunNo(
												kmbHykHaiAR.getHyokaKijunNo());
						
						if (kmzHykARList != null && kmzHykARList.size() != 0) {
							for (int i = 0; i < kmzHykARList.size(); i++) {
								kmzHykAR = (KmzHykAR)kmzHykARList.get(i);
								if (intSoten >= kmzHykAR.getSotenFrom().intValue()
								 &&	intSoten <= kmzHykAR.getSotenTo().intValue()) {
									ret = kmzHykAR.getHyokaCd();
									break;
								}
							}
						}
					}
				}
			}
		} catch (DbException e) {
			throw new RuntimeException(e);
		}	        
    	return ret;
    }
    
    /**
     * 素点を評価略称に変換します
     * 
     * @param nyugakNendoCur
     * @param nyugakGakkiNoCur
     * @param curGakkaCd
     * @param soten
     * @return
     */
    protected String cnvHyokaRyak(int nyugakNendoCur,
    						   int nyugakGakkiNoCur,
							   String curGakkaCd,
							   String soten) {

    	String ret = "";
    	int intSoten = 0;

		if (UtilStr.cnvNull(soten).equals("")) {
    		return ret;
    	}else if(UtilStr.chkHankakuNumberMoji(soten) == false){
//    		入力素点は数字がない
    		String str = cnvSoten(nyugakNendoCur,nyugakGakkiNoCur,curGakkaCd,soten);
    		//入力素点があります
    		if(!"".equals(str.trim())){
//    			入力素点は大于100
    			if(str.length()>1){
    				return String.valueOf(str.charAt(0));
    			}
    			return soten;
    		}
    		return str;
    	} else {
    		intSoten = Integer.valueOf(soten).intValue();
    	}
    	
    	
		try {
			// 評価基準配当
			KmbHykHaiUPDAO kmbHykHaiUPDAO = (KmbHykHaiUPDAO) getDbs().getDao(KmbHykHaiUPDAO.class);
			KmbHykHaiAR kmbHykHaiAR;

			// 評価基準
			KmzHykUPDAO kmzHykUPDAO = (KmzHykUPDAO) getDbs().getDao(KmzHykUPDAO.class);
			KmzHykAR kmzHykAR;

			kmbHykHaiAR = kmbHykHaiUPDAO.findByPrimaryKey(
					nyugakNendoCur,
					nyugakGakkiNoCur,
					curGakkaCd);
	
			if (kmbHykHaiAR != null) {
				List kmzHykARList = kmzHykUPDAO.findByHyokaKijunNo(
										kmbHykHaiAR.getHyokaKijunNo());
				
				if (kmzHykARList != null && kmzHykARList.size() != 0) {
					for (int i = 0; i < kmzHykARList.size(); i++) {
						kmzHykAR = (KmzHykAR)kmzHykARList.get(i);
						if (intSoten >= kmzHykAR.getSotenFrom().intValue()
						 &&	intSoten <= kmzHykAR.getSotenTo().intValue()) {
							ret = isDspHyoka ? kmzHykAR.getHyokaCd() : 
								kmzHykAR.getHyokaNameRyak();
							break;
						}
					}
				}
			}
		} catch (DbException e) {
			throw new RuntimeException(e);
		}	        
    	return ret;
    }

    /**
     * 評価コードを素点に変換します
     * 
     * @param nyugakNendoCur
     * @param nyugakGakkiNoCur
     * @param curGakkaCd
     * @param soten
     * @return
     */
    protected String cnvSoten(int nyugakNendoCur,
    						   int nyugakGakkiNoCur,
							   String curGakkaCd,
							   String hyoka) {

    	String ret = "0";

		try {
			// 評価基準配当
			KmbHykHaiUPDAO kmbHykHaiUPDAO = (KmbHykHaiUPDAO) getDbs().getDao(KmbHykHaiUPDAO.class);
			KmbHykHaiAR kmbHykHaiAR;

			// 評価基準
			KmzHykUPDAO kmzHykUPDAO = (KmzHykUPDAO) getDbs().getDao(KmzHykUPDAO.class);
			KmzHykAR kmzHykAR;

			kmbHykHaiAR = kmbHykHaiUPDAO.findByPrimaryKey(
					nyugakNendoCur,
					nyugakGakkiNoCur,
					curGakkaCd);
			
			if (kmbHykHaiAR != null) {
				List kmzHykARList = kmzHykUPDAO.findByHyokaKijunNo(
										kmbHykHaiAR.getHyokaKijunNo());
				
				if (kmzHykARList != null && kmzHykARList.size() != 0) {
					for (int i = 0; i < kmzHykARList.size(); i++) {
						kmzHykAR = (KmzHykAR)kmzHykARList.get(i);
						//データベースでは評価があります
						if(hyoka.equals(kmzHykAR.getHyokaCd()) ){
							//評価の素点大于100、評価+素点を返事
							if(kmzHykAR.getSotenFrom().intValue() >100){
									return hyoka + String.valueOf(kmzHykAR.getSotenTo());			
							}	
							//評価の素点小于100,評価を返事
								return hyoka;
						}	
					}
					//データベースでは評価がありません
					return "";
				}
			}

		} catch (DbException e) {
			throw new RuntimeException(e);
		}	        

    	return ret;
    }
    
    /**
     * 画面再描画用に評価コードまたは素点に対応する評価略称に変換します
     * 
     * @param nyugakNendoCur
     * @param nyugakGakkiNoCur
     * @param curGakkaCd
     * @param hykCd
     * @return
     */
    protected String cnvHyokaRyakForReset(int nyugakNendoCur,
    						   int nyugakGakkiNoCur,
							   String curGakkaCd,
							   String hykCd) {

    	String ret = "";
    	int intSoten = 0;
    	int kbn = 0;
		
    	if (UtilStr.cnvNull(hykCd).equals("")
    	 || UtilStr.cnvTrim(hykCd).equals("")) {
    		return ret;
    	}
		
        String sotenHankakuString = UtilStr.cnvHankaku(hykCd);
        String sotenString = UtilStr.cnvTrim(sotenHankakuString);
    	
		try {
			// 評価基準配当
			KmbHykHaiUPDAO kmbHykHaiUPDAO = (KmbHykHaiUPDAO) getDbs().getDao(KmbHykHaiUPDAO.class);
			KmbHykHaiAR kmbHykHaiAR;

			// 評価基準
			KmzHykUPDAO kmzHykUPDAO = (KmzHykUPDAO) getDbs().getDao(KmzHykUPDAO.class);
			KmzHykAR kmzHykAR;
			
			if (sotenAtukai.equals(SOTEN)) {
				if (UtilStr.chkHankakuNumberMoji(sotenString)) {
					kbn = 1;
				}
			} else if (sotenAtukai.equals(HYOKA)) {
				if (UtilStr.chkHankakuNumberMoji(sotenString)) {
					if (Integer.valueOf(sotenString).intValue() <= 100) { 
						kbn = 1;
					} else {
						// 101点以上の場合は変換しない
						return ret;
					}
				} else {
					kbn = 2;
				}
			}
			if (kbn == 1) {
				// パラメータが0:素点として登録の場合で数値で入力されている場合、
				// またはパラメータが1:評価として登録の場合で数値で入力されている場合
				intSoten = Integer.valueOf(sotenString).intValue();

				kmbHykHaiAR = kmbHykHaiUPDAO.findByPrimaryKey(
														nyugakNendoCur,
														nyugakGakkiNoCur,
														curGakkaCd);
				
				if (kmbHykHaiAR != null) {
					List kmzHykARList = kmzHykUPDAO.findByHyokaKijunNo(
											kmbHykHaiAR.getHyokaKijunNo());
					
					if (kmzHykARList != null && kmzHykARList.size() != 0) {
						for (int i = 0; i < kmzHykARList.size(); i++) {
							kmzHykAR = (KmzHykAR)kmzHykARList.get(i);
							if (intSoten >= kmzHykAR.getSotenFrom().intValue()
							 &&	intSoten <= kmzHykAR.getSotenTo().intValue()) {
								ret = isDspHyoka ? kmzHykAR.getHyokaCd() : 
									UtilStr.cnvNull(kmzHykAR.getHyokaNameRyak());
								break;
							}
						}
					}
				}
			} else if (kbn == 2) {
				// パラメータが1:評価として登録の場合で文字で入力されている場合
				kmbHykHaiAR = kmbHykHaiUPDAO.findByPrimaryKey(
														nyugakNendoCur,
														nyugakGakkiNoCur,
														curGakkaCd);
				
				if (kmbHykHaiAR != null) {
					kmzHykAR = kmzHykUPDAO.findByPrimaryKey(
											kmbHykHaiAR.getHyokaKijunNo(),
											sotenString);
					if (kmzHykAR != null
					 && kmzHykAR.getSotenFrom().intValue() >= 101) {
						// 素点FROMが101点以上の場合のみ評価略称を設定する
						ret = isDspHyoka ? kmzHykAR.getHyokaCd() : 
							UtilStr.cnvNull(kmzHykAR.getHyokaNameRyak());
					}
				}
			}
		} catch (DbException e) {
			UtilLog.error(this.getClass(),e);
			//例外発生時は処理しない
			return null;
		}	        
    	return ret;
    }
    
    /**
     * 子画面オープン受け取りメソッド
     * 
     * @param pagecode
     * @return
     */
    protected String detail(PageCodeBaseEx pagecode) {

        Kmc00202A pc = (Kmc00202A) pagecode;

        // 評価略称再セット
        resethyokaName(pc);
        
        Kmc00202AL01Bean listBean = (Kmc00202AL01Bean) pc
                .getPropSaitenTorokuTable().getRowData();

// UPEX-1237 2009/11/09 y-matsuda upd Start
    	// 人事コードを暗号化
		UtilCrypt utilCrypt  = new UtilCrypt();
		String kanriNo = utilCrypt.encryptToStr(String.valueOf(listBean.getKanriNo()));

    	// JSP内hiddenへ管理番号をセット
        pc.getPropKanriNoHidden().setStringValue(kanriNo);
//        pc.getPropKanriNoHidden().setStringValue(
//                String.valueOf(listBean.getKanriNo()));
// UPEX-1237 2009/11/09 y-matsuda upd End
        
        return UpActionConst.RET_TRUE;
    }

    // 6/29修正St↓↓↓↓↓↓↓↓
    /**
     * 
     * エラー判定（素点TOチェック）
     * 
     * @param service
     *            採点サービス
     * @param kanriNo
     *            管理番号
     * @param shikenFlg
     *            試験区分
     * @param soten
     *            素点
     * @return hantei 素点TOの判定結果（true:OK、false：NG）
     */
    private boolean checkMaxSoten(SaitenService service, Long kanriNo,
            int shikenFlg, String soten) {

        if (shikenFlg == ShikenUPKbn.TEIKISHIKEN.getCode()) {
            // 定期試験の場合、trueにてチェックを行わない。
            return true;
        }

        ShikenBetuHyokaKijunCondition condition = new ShikenBetuHyokaKijunCondition();
        condition.setKanriNo(kanriNo);
        condition.setShikenKbn(String.valueOf(shikenFlg));
        boolean hantei = false;
        try {
            int sotenToMax = service.acquireShikenBetuSotenMax(condition);
            if (sotenToMax >= Integer.parseInt(soten)) {
                hantei = true;
            }
        } catch (NoSuchDataException e) {
// 2007/02/15 不具合管理一覧：No.3201 Start -->>
// 対象データが１件もない場合は、この処理が行われる。
// 対象データが１件もない場合は制限チェックをかけない為、結果OKで戻す。
//            hantei = false;
            hantei = true;
// <<-- End   2007/02/15 不具合管理一覧：No.3201
        }
        return hantei;
    }
    // 6/29修正En↑↑↑↑↑↑↑↑
// 障害対応 UPEX-153 採点登録画面で出欠率を授業単位で表示する為追加 2009.02.04 Takemoto add Start
    /**
     *
     * 出欠率の集約処理を行う 
     * 
     */
    private void shuyakuSyukkesseki() {
    	
        if( PearentShukkessekiList == null || PearentShukkessekiList.isEmpty() ){ return; }
        
        SyuYaKuMap = null;
        SyuYaKuMap = new HashMap();
        
        for( int i = 0 ; i < PearentShukkessekiList.size() ; i++ ){
        	
        	List shukkessekiList = (List)PearentShukkessekiList.get(i);
        	
        	if( shukkessekiList == null || shukkessekiList.isEmpty() ) { continue; }
        	
        	for( int j = 0 ; j < shukkessekiList.size() ; j++ ){
        		ShukkessekiDTO newDto = (ShukkessekiDTO) shukkessekiList.get(j);
        		
        		Long newKanriNo = newDto.getKanriNo();
        		
				// 管理番号(Key)の存在チェック
				if( SyuYaKuMap.containsKey( newKanriNo )){
					
					// 管理番号(Key)が既に存在した場合は、各回数の加算処理を行う
					ShukkessekiDTO keyDto = (ShukkessekiDTO)SyuYaKuMap.get(newDto.getKanriNo());
					setAppendKaisu(keyDto,newDto);
					
				}else{
					
					// 管理番号(Key)が無ければ新規登録
					SyuYaKuMap.put(newDto.getKanriNo(),newDto);
				}
        	}
        }
        
    }
    	
	/**
	 * 出欠情報の各回数の加算処理を行う(出欠情報授業単位用取得用)
	 * 
	 * @param	keyDto	加算先(Key)の出欠情報DTO
	 * 			newDto	新規の出欠情報DTO
	 * 
	 * @return List パラメータリスト
	 * @throws GakuenException
	 * @throws DbException
	 */
	public void setAppendKaisu(ShukkessekiDTO keyDto,ShukkessekiDTO newDto){
		
		// ===================================================================
		// 加算先(Key)の出欠情報DTOの各回数の取得
		// ===================================================================
		int keyKessekiKaisu = keyDto.getKessekiKaisu(); 				// 加算先DTO：欠席回数を取得
		int keyJugyoKaisu = keyDto.getJugyoKaisu(); 					// 加算先DTO：授業回数を取得
		
		// ===================================================================
		// 新規の出欠情報DTOの各回数の取得
		// ===================================================================
		int newKessekiKaisu = newDto.getKessekiKaisu(); 				// 新規DTO：欠席回数を取得
		int newJugyoKaisu = newDto.getJugyoKaisu(); 					// 新規DTO：授業回数を取得
		
		// ===================================================================
		// 各回数を加算して加算先(Key)の出欠情報ARにセットする
		// ===================================================================
		keyDto.setKessekiKaisu( keyKessekiKaisu + newKessekiKaisu );	// 欠席回数を加算して格納
		keyDto.setJugyoKaisu( keyJugyoKaisu + newJugyoKaisu );			// 授業回数を加算して格納
		
	}
// 障害対応 UPEX-153 採点登録画面で出欠率を授業単位で表示する為追加 2009.02.04 Takemoto add End 
}