/*
 * �쐬��: 2006/04/10
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
 * �̓_�o�^��ʗp�A�N�V���� <br>
 * 
 * @author JApan System Techniques Co.,Ltd. <br>
 */
public class Kmc00202AAct extends UpActionBase {

    /** �\���{�^�� */
    public static final String ACTION_DISPLAY = "display";
//��QNO4082 �o���p�t�H�[�}���X�A�b�v���ʉ� 2007.08.20 Horiguchi Start
    private List ShukkessekiList = null;
//��QNO4082 �o���p�t�H�[�}���X�A�b�v���ʉ� 2007.08.20 Horiguchi End
    
// ��Q�Ή� UPEX-153 �̓_�o�^��ʂ̏o���������ƒP�ʂŕ\������גǉ� add Start
    private List PearentShukkessekiList = null;	// �o���ȏ�񃊃X�g�i�[�p���X�g
    private Map SyuYaKuMap = null;					// �o�����W��p�}�b�v
// ��Q�Ή� UPEX-153 �̓_�o�^��ʂ̏o���������ƒP�ʂŕ\������גǉ� add End
    
    private String sotenAtukai = "";
    private String misaitenChk = "";
    public boolean semesterDsp = false;
    public static final String SOTEN = "0";
    public static final String HYOKA = "1";
    public static final String NO_CHK = "0";
    public static final String CHK = "1";
    public static final String NO_DSP = "0";
    public static final String DSP = "1";
    
    // �]���R�[�h��\�����邩�A�]�����̂�\�����邩
    // true:�]���R�[�h false:�]������
    private boolean isDspHyoka = true;
    
    /**
     * ��ʂ̏����\�����s���܂� <br>
     * 
     * @param pagecode
     *            �y�[�W�R�[�h
     * @return �g�����U�N�V���������̌���
     */
    protected String init(PageCodeBaseEx pagecode) {

        Kmc00202A pc = (Kmc00202A) pagecode;
        Kmc00201A pagePc = (Kmc00201A) UtilSystem
                .getManagedBean(Kmc00201A.class);
        
        if (pc instanceof Jgc91102A) {
        	 pagePc = (Kmc00201A) UtilSystem
             .getManagedBean(Jgc91101A.class);
        }

        // �p�����[�^�̎擾
        setParam();
        
		// �m�艟�����ɖ��̓_�o�^�f�[�^���݃`�F�b�N���s��
		pc.getPropExecutableFixed().setIntegerValue(new Integer(0));

        //�\�����я��Z�b�g
        narabijunBind(pc);

        // ���v�O���t�\������
        setSokeiGraphFlag(pc);

// 2008-02-08 UPEX-250 START        
        //�w�ȕ\���敪�̎擾
        setDspGakka(pc);
// 2008-02-08 UPEX-250 END        

        //��ʂ̏����̃f�[�^�����
        setData(pagePc, pc);
        
        //�q��ʂ̃t�H�[�J�X�p�t���O�̏�����
        pc.setFocusFlg(true);
        
        // �o�^�������ŁA������ʂ̎g�p�ɂ��A��ʂƃT�[�o�̓��e���قȂ��Ă��Ȃ������`�F�b�N���邽�߁A
        // ��ʂɉB�����ځi�N�x�A�w���A���ƃR�[�h�A�����敪�A�����񐔂̌���������j��ݒ�
        pc.getPropBrowserHidden().setStringValue(pc.getPropNendo().getStringValue() 
        		+ "|" + pc.getPropGakki().getStringValue()+ "|" + pc.getJugyoCd() 
				+ "|" + pc.getPropShikenKubun().getStringValue() + "|" + UtilStr.cnvNull(pc.getShikenKaisu()));
        
        return UpActionConst.RET_TRUE;
    }

    /**
	 * �p�����[�^�e�[�u�����f�_�������@�A���̓_�҃`�F�b�N�A�Z���X�^�̕\���ۂ̐ݒ�l���擾���܂��B<br>
	 */
	private void setParam() {

		try {
			// �p�����[�^DAO
			CouParamDAO couParamDAO = (CouParamDAO) getDbs().getDao(CouParamDAO.class);
			CouParamAR couParamAR;
			
			// �f�_�������@
			couParamAR = couParamDAO.findByPrimaryKey("KMC", "SOTEN_ATUKAI_FLG", 0);
			sotenAtukai = "0";
			if (couParamAR != null) {
				if (!UtilStr.cnvNull(couParamAR.getValue()).equals("")
		        && (couParamAR.getValue().equals(SOTEN)
		         || couParamAR.getValue().equals(HYOKA))) {
		        	sotenAtukai = couParamAR.getValue();
		        }
			}

			// ���̓_�҃`�F�b�N
			couParamAR = couParamDAO.findByPrimaryKey("KMC", "MISAITEN_MSG_FLG", 0);
			misaitenChk = "0";
			if (couParamAR != null) {
				if (!UtilStr.cnvNull(couParamAR.getValue()).equals("")
		        && (couParamAR.getValue().equals(CHK)
		         || couParamAR.getValue().equals(NO_CHK))) {
					misaitenChk = couParamAR.getValue();
		        }
			}
			
			// �Z���X�^�̕\����
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
			
			// �]���R�[�h��\�����邩�A�]�����̂�\�����邩
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
     * �]��������ʕ\�����s���܂��B <br>
     * 
     * @param pagecode
     *            �y�[�W�R�[�h
     * @return �g�����U�N�V���������̌���
     */
    protected String hyokaDisplay(PageCodeBaseEx pagecode) {

    	Kmc00202A pc = (Kmc00202A) pagecode;
	    Kmc00201A pagePc = (Kmc00201A) UtilSystem
	            .getManagedBean(Kmc00201A.class);
	    
        // ��ʂ̉B�����ځi�N�x�A�w���A���ƃR�[�h�A�����敪�A�����񐔂̌���������j���擾
        String browserHidden = pc.getPropBrowserHidden().getStringValue();
        // �T�[�o�̍��ځi�N�x�A�w���A���ƃR�[�h�A�����敪�A�����񐔂̌���������j���擾
        String pcItem = pc.getPropNendo().getStringValue() + "|" +  pc.getPropGakki().getStringValue() 
							+ "|" +  pc.getJugyoCd() + "|" +  pc.getPropShikenKubun().getStringValue() 
							+ "|" +  UtilStr.cnvNull(pc.getShikenKaisu());
        // ������ʂ̎g�p�ɂ��A��ʂƃT�[�o�̓��e���قȂ��Ă��Ȃ������`�F�b�N
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

        // �]�����̍ăZ�b�g
        resethyokaName(pc);
	    
        final StringBuffer rowClass = new StringBuffer();
        final SaitenService saitenService = new SaitenService(this);
        final JugyoService jugyoService = new JugyoService(this);
        
        //��s�Ή�
        final List dataList = pc.getPropSaitenTorokuTable().getNoEmptyList();

        int rowCnt = getRow(pc);

        int errCount = 0;

		final Iterator ite = dataList.iterator();
        //��ʂ̃f�[�^�[�����
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

            //��������ȊO�����񐔃Z�b�g
            if (pc.getShikenKaisu() != null) {
                hyokaKoshinValue.setSikenKaisu(Integer.valueOf(pc
                        .getShikenKaisu()));
            }
            hyokaKoshinValue.setSitnUpdateDate(DateFactory.getInstance());

            //�f�_�^�p�̏ꍇ
            hyokaKoshinValue.setSaitenKbn(String.valueOf(SaitenKbn.SOTEN
                    .getCode()));

            // �`�F�b�N�̃��b�Z�[�W
            String message = "";

            //�f�_�̓��̓`�F�b�N���s��
            message = acquireCheckMsg(jugyoService, listBean,
                    saitenService, pagePc);

            listBean.setMessage(message);

            //�G���[�����J�E���g
            if (message.length() > 0) {
                errCount++;
            }
            //�G���[���b�Z�[�W���Ȃ��A�܂��͑f�_�����͔͂w�i��
            if (message.length() != 0
                    || (listBean.getSoten() == null
                    || UtilStr.cnvTrim(listBean.getSoten()).length() == 0)) {

                rowClass.append(", rowClass1");
            } else {
                rowClass.append(", selectiveLine");
            }
        }

        if (errCount != 0) {
            // ���b�Z�[�W�u�G���[������܂��̂Ŋm�F���Ă��������v
            UtilSystem.getDisplayInfo().setDisplayMessage(
                    UtilUpMsg.editMsg(UtilProperty
                            .getMsgString("KMC_MSG_0009E")));
            return UpActionConst.RET_TRUE;
        }

        //�I�v�V�����e�[�u���Ɍ���������ێ�������B
        saveDefaultItemValue(pc);

		// �q��ʋN���t���O��ON�ɂ���B
		pc.getPropKogamenOpenFlg().setStringValue("1");
 
        return UpActionConst.RET_TRUE;
    }

    /**
     * �̓_�ꗗ�v���r���[��ʕ\�����s���܂��B <br>
     * 
     * @param pagecode
     *            �y�[�W�R�[�h
     * @return �g�����U�N�V���������̌���
     */
    protected String saitenDisplay(PageCodeBaseEx pagecode) {

    	Kmc00202A pc = (Kmc00202A) pagecode;
   	
        //�I�v�V�����e�[�u���Ɍ���������ێ�������B
        saveDefaultItemValue(pc);

    	PKmc0203A.sotenInsatuOpen();
		pc.setFocusFlg(false);
		
		return UpActionConst.RET_TRUE;
    }

    /**
     * �̓_�ꗗ�v���r���[��ʕ\�����s���܂��B(�N���X�v���t�@�C���p) <br>
     * 
     * @param pagecode
     *            �y�[�W�R�[�h
     * @return �g�����U�N�V���������̌���
     */
    protected String saitenDisplayForClass(PageCodeBaseEx pagecode) {

    	Kmc00202A pc = (Kmc00202A) pagecode;

        //�I�v�V�����e�[�u���Ɍ���������ێ�������B
        saveDefaultItemValue(pc);

    	PKmc0203A.sotenInsatuOpenForClass();
		pc.setFocusFlg(false);
		
		return UpActionConst.RET_TRUE;
    }
	
//  V1.2�Ή� 2009/11/22 k.higashida Start
	/**
	 * �f�_�̏ꍇ�A��ʂ̒ǍĎ�������A�̓_�o�^��ʂ̃f�[�^��\�����܂��B<br>
	 * 
	 * @param pc �̓_���ƈꗗ�̃y�[�W�R�[�h
	 * @param nextPc �̓_�o�^��ʂ̃y�[�W�R�[�h
	 * @param shikenFlg true:�ǎ��� false:�Ď���
	 * @return �������̂Ǝ�����
	 */
	private String getSotenTuiSaiShikenValue(Kmc00201A pc,
			Kmc00202A nextPc, boolean shikenFlg) {
		//�������́F�ǎ����A�Ď���
		String shikenName = "";
		//������
		String shikenKaisu;
		
		final String kaisu = UtilProperty.getMsgItemString("CO_Times");
		Kmc00201AL02Bean bean = null;
		
		if (shikenFlg) {
			//itemKM_ja.properties�t�@�C������ �h�ǎ����h���擾���܂��B
//			 V1.2�Ή� 2009/11/22 k.higashida Start
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
//				 V1.2�Ή� 2009/11/22 k.higashida End
				bean = (Kmc00201AL02Bean) pc.getPropTuiShikenTable().getRowData();
			}
		}
		
		shikenKaisu = bean.getShikenKaisu();
		
		nextPc.setJugyoCd(bean.getJugyoCode());
		nextPc.setShikenKaisu(bean.getShikenKaisu());
		nextPc.getPropKamokumei().setStringValue(bean.getKamokName());
		//// 6/22 �s��Ή� Start����������
		nextPc.getPropKamokumei().setStringValue(bean.getJugyoKamokName());
		//// 6/22 �s��Ή� End������������		
		if (shikenKaisu == null) {
			shikenKaisu = "";
		} else {
			shikenKaisu = "(" + shikenKaisu + kaisu + ")";
		}
		
		shikenName = shikenName + shikenKaisu;
		return shikenName;
	}
	
	/**
	 * �̓_�^�p�O�`�F�b�N���s���܂��B<br>
	 * 
	 * @param JugyoCd ���ƃR�[�h
	 * @param kaikoNendo �J�u�N�x
	 * @param gakkiNo �w��No
	 * @param shikenKbn �����敪
	 * @param shikenKaisu ������
	 * @return �^�p��:true�A�^�p�O:false
	 */
	private boolean chkUnyo(String jugyoCd, int kaikoNendo,
							  int gakkiNo, String shikenKbn, int shikenKaisu) {
		
		boolean booRet = false;

		// ���ݓ�
		Calendar cal = Calendar.getInstance();
		Date today = new Date(cal.getTimeInMillis()); 

        //�@�̓_�^�pDAO
    	KmcStnUnyoDAO kmcStnUnyoDAO = (KmcStnUnyoDAO)getDbs().
											getDao(KmcStnUnyoDAO.class);
        // �̓_�^�pAR
    	KmcStnUnyoAR kmcStnUnyoAR;
		
        // ����DAO
    	KmdJugyDAO kmdJugyDAO = (KmdJugyDAO)getDbs().
											getDao(KmdJugyDAO.class);
        // ����AR
    	KmdJugyAR kmdJugyAR;
		
		try {
			// ���Ƃ̕����R�[�h���擾
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
					// �̓_�^�p���ԓ�
					booRet = true;
				} else {
					// �̓_�^�p���ԊO
					booRet = false;
				}
			}				
				
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		}
		return booRet;
		
	}
//  V1.2�Ή� 2009/11/22 k.higashida End

	
    /**
     * �Č����������s���܂��B <br>
     * �o�^�A�X�V��ʂ��ꗗ��ʂɖ߂��܂��B
     * 
     * @param pagecode
     *            �y�[�W�R�[�h
     * @return �g�����U�N�V���������̌���
     */
    protected String display(PageCodeBaseEx pagecode) {
        Kmc00202A pc = (Kmc00202A) pagecode;
        Kmc00201A pagePc = (Kmc00201A) UtilSystem
                .getManagedBean(Kmc00201A.class);
        
        if (pc instanceof Jgc91102A) {
       	 pagePc = (Kmc00201A) UtilSystem
            .getManagedBean(Jgc91101A.class);
       }
        
        //�I�v�V�����e�[�u���Ɍ���������ێ�������B
        saveDefaultItemValue(pc);
        //�����f�[�^�����
        setData(pagePc, pc);
        pc.getPropValueChanged().setValue(Boolean.FALSE);
        pc.getPropHasInputError().setValue(Boolean.FALSE);
        // 6/22�C��Start��������������
        // return UpActionConst.RET_TRUE;
        pc.getPropSaitenTorokuTable().setFirst(0);
        
        if (pc.getHtmlSaitenTorokuTable() !=  null) {
        	pc.getHtmlSaitenTorokuTable().setFirst(0);
        }
        
        return pc.getFormId();
        // 6/22�C��End����������������
    }

    /**
     * �f�[�^���쐬���܂��B <br>
     * 
     * @param pagePc
     *            Kmc00201A�y�[�W�R�[�h
     * @param pc
     *            Kmc00202A�y�[�W�R�[�h
     */
    protected void setData(Kmc00201A pagePc, Kmc00202A pc) {
        final List rowList = new ArrayList();
        final StringBuffer rowClass = new StringBuffer();
        final List listData = getJyugyoRisyu(pagePc, pc);

        //�󔒍s���������\��
        pc.getPropSaitenTorokuTable().setListbean(new Kmc00202AL01Bean());

// 2008-02-08 UPEX-250 START
		// �w�Ȏ�ʋ敪���擾
		String dspGakka = pc.getDspGakka();		
		// ���x���ݒ�
		if (dspGakka.equals("0")) {
			// �����w�ȑg�D��\��
			pc.setGakkaLabel(UtilProperty.getMsgItemString("KM_ShozokuGakka"));
		} else {
			// �J���L�������w�ȑg�D��\��
			pc.setGakkaLabel(UtilProperty.getMsgItemString("KM_Gakka"));
		}
// 2008-02-08 UPEX-250 END

        // ���O�Ώۂ̈ٓ��敪���擾
        final SaitenService saitenService = new SaitenService(this);
        
// ���� 2009.10.05 h.matsuda ��Q�Ή�UPEX-1126 del start	        
//        final List jogaiTaishoIdoKubunList = saitenService
//                .listJogaiTaishoIdoKubun(makeJogaiTaishoIdoKubunCondition(
//                        pagePc, pc));
// ���� 2009.10.05 h.matsuda ��Q�Ή�UPEX-1126 del end 
        // ���ݓ��t�Ŋw���̈ٓ��o�w��������
        final GakuseiService gakuseiService = new GakuseiService(this);
        final Map idoShutsugakuInformationMap = searchIdoShutsugakuInformations(gakuseiService);

        //��ʂ̃f�[�^�����
        if (listData != null && listData.size() > 0) {
            final SeisekiService seisekiService = new SeisekiService(this);
//��QNO4082 �o���p�t�H�[�}���X�A�b�v���ʉ� 2007.08.20 Horiguchi Start
            ShukkessekiList = null;
            //�o���Ǘ�����ꍇ
            if (getAcquireShukketsuKanriBoolean(pagePc, pc)) {
                ShukkessekiList = new ArrayList();
	            try{
// ��Q�Ή� UPEX-153 �̓_�o�^��ʂ̏o���������ƒP�ʂŕ\������׏C�� 2009.02.04 Takemoto Start
//		            //���ƔN�x(���ƊJ�n�N�x)�擾
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
//						    //���Ƃɑ΂���o�����擾
//							ShukkessekiList = shukketsuService.listShukkessekiInfo(condition);
//						}
//				        catch(Exception e){
//				            UtilLog.error( this.getClass(),"�o�����擾�s��");
//				        }
	            	
	            	// ==============================================================
					// �Ώێ��Ƃ̊w�������̏o�������擾����
	            	// ==============================================================
		            // ���ƔN�x(���ƊJ�n�N�x)�擾
					KmdJgkmUPDAO jugyKmDAO = (KmdJgkmUPDAO)this.getDbSession().getDao(KmdJgkmUPDAO.class);
					List JgkmListForJugyoNendo = jugyKmDAO.findByKaikoNendoGakkiNoJugyoCd(
												Integer.parseInt(pagePc.getNendo()),
												Integer.parseInt(pagePc.getPropTempgakkiNo().getStringValue()),
												pc.getJugyoCd());
					
					if (JgkmListForJugyoNendo != null && JgkmListForJugyoNendo.size() != 0) {
						
						// ���ƃR�}������ƔN�x���擾���A���ƔN�x�A���ƃR�[�h�ɕR�t���R�}�̎擾���s��
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
					        
							// �o���ȃ��X�g���i�[���郊�X�g
							PearentShukkessekiList = new ArrayList();
							
							// �N�x�A�w���̑O��l�i�[�p�ϐ�
							int bfNendo = 0;
							int bfGakki = 0;
							
							// �Ώێ��Ƃ̊w�������̏o�������擾����
							for( int i = 0 ; i < jugyKmList.size() ; i++ ){
								
								KmdJgkmUPAR jugyKmAR = (KmdJgkmUPAR)jugyKmList.get(i);
								
								// �J�u�N�x�Ɗw�����O��l�Ɠ����ł���΃X�L�b�v
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
								    //���Ƃɑ΂���o�����擾
									ShukkessekiList = shukketsuService.listShukkessekiInfo(condition);
								}
						        catch(Exception e){
						            UtilLog.error( this.getClass(),"�o�����擾�s��");
						        }
						        // �擾�����o���ȃ��X�g���i�[
						        PearentShukkessekiList.add(ShukkessekiList);
							}
							
							// �W�v�������s��
							shuyakuSyukkesseki();
							
						}else {
						    UtilLog.error( this.getClass(), "���ƃR�}�擾�s��" );
			            }
// ��Q�Ή� UPEX-153 �̓_�o�^��ʂ̏o���������ƒP�ʂŕ\������׏C�� 2009.02.04 Takemoto End
					} else {
					    UtilLog.error( this.getClass(), "���ƃR�}�擾�s��" );
		            }
	            } catch (Exception e) {
	                UtilLog.error( this.getClass(), "���ƃR�}�擾�s��" );
	            }
            }
//��QNO4082 �o���p�t�H�[�}���X�A�b�v���ʉ� 2007.08.20 Horiguchi End
            final Iterator ite = listData.iterator();
            for (int i = 0; ite.hasNext(); i++) {
                final JugyoRisyuDTO dto = (JugyoRisyuDTO) ite.next();

                boolean jogaiTaisho = false;
                String biko = "";

                final Long kanriNo = dto.getKanriBangou();
                if (kanriNo != null) {
                    // �����@�\�L�q:�y2-2�Ń��R�[�h���擾���ꂽ�ꍇ�z
                    if (idoShutsugakuInformationMap.containsKey(kanriNo)) {
                        final IdoShutsugakuInformationDTO information = (IdoShutsugakuInformationDTO) idoShutsugakuInformationMap
                                .get(kanriNo);

// ���� 2009.10.05 h.matsuda ��Q�Ή�UPEX-1126 del start	                   
//                        jogaiTaisho = jogaiTaishoIdoKubunList
//                                .contains(information.getShubetsuKubun());
// ���� 2009.10.05 h.matsuda ��Q�Ή�UPEX-1126 del end 
                        
                        // �����@�\�L�q:2-2�Ŏ擾�����ٓ��敪���A2-1�̈ٓ��敪���X�g�ɑ��݂��Ȃ��ꍇ
                        if (!jogaiTaisho) {
                            // ���l�i�Ȃ����̓��b�Z�[�W�j��ݒ�
                            biko = information.getShubetsuName();
                        }
                    }
                }

                // ���O�ΏۂłȂ���΃��X�g�֒ǉ�
                if (!jogaiTaisho) {
                    final Kmc00202AL01Bean bean = makeKmc00202AL01Bean(
                            seisekiService, saitenService, pagePc, pc, dto,
                            rowClass);
                    // ���l�i�Ȃ����̓��b�Z�[�W�j�̐ݒ�
                    bean.setBiko(biko);

                    rowList.add(bean);
                }
            }

            // �l���Ɖ�̃����N�ېݒ�
            GakuseiInfoLinkChecker checker = new GakuseiInfoLinkChecker();
            String jinjCd = UtilUpSystem.getUpSystemData().getLoginUserBean()
                    .getJinjiCd();
            checker.setLinkInfo(rowList, jinjCd, getDbs());

            // �ꗗ�ɋ�s���ł���ꍇ ��s�ɃX�^�C����ݒ�
            final int rowCnt = getRow(pc);
            if (listData.size() % rowCnt > 0) {
                for (int i = 0; i < rowCnt - (listData.size() % rowCnt); i++) {
                    rowClass.append(",rowClass1");
                }
            }

            pc.getPropSaitenTorokuTable().getColumnClasses();

            //�T�[�r�X����A�ꗗ�f�[�^�擾����
            pc.getPropSaitenTorokuTable().setList(rowList);

            //�ꗗ�̌������擾����
            pc.getPropSaitenTorokuTable().setRows(rowCnt);
            final String rowClasses = rowClass.substring(1);
            pc.getPropSaitenTorokuTable().setRowClasses(rowClasses);
            pc.getPropSaitenTorokuTable().setStockRowClasses(rowClasses);
            pc.getPropSaitenTorokuTable().setRendered(true);

            
            pc.setSyussekiFlag(getAcquireShukketsuKanriBoolean(pagePc, pc));
            pc.setSemesterFlag(semesterDsp);
            if (semesterDsp) {
                //�o�ȗ�����̏ꍇ
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
                //�o�ȗ��Ȃ��̏ꍇ
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
                //�o�ȗ�����̏ꍇ
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
                //�o�ȗ��Ȃ��̏ꍇ
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
     * �T�[�r�X����A�ꗗ�f�[�^�擾���܂��B <br>
     * 
     * @param pagePc
     *            Kmc00201A�y�[�W�R�[�h
     * @param pc
     *            Kmc00202A�y�[�W�R�[�h
     * @return �ꗗDTO�̃��X�g
     */
    private List getJyugyoRisyu(Kmc00201A pagePc, Kmc00202A pc) {
        final JugyoService jugyoService = new JugyoService(this);

        //�����������Z�b�g
        final JugyoRishuCondition condition = new JugyoRishuCondition();
        condition.setNendo(Integer.valueOf(pagePc.getPropNendo()
                .getStringValue()));
        condition.setGakkiNo(Integer.valueOf(pagePc.getPropGakkiNo()
                .getStringValue()));
        condition.setJugyoCd(pc.getJugyoCd());
        condition.setShikenKbn(new Integer(pagePc.getShikenFlg()));
        //�����敪�͒���������Ȃ�
        if (pagePc.getShikenFlg() != ShikenUPKbn.TEIKISHIKEN.getCode()) {
            condition.setSikenKaisu(Integer.valueOf(pc.getShikenKaisu()));
        }
        condition.setNarabijun(pc.getPropRow().getStringValue());
        condition.setHyoji(pc.getPropDisplay().getStringValue());
        //�����R�[�h���Z�b�g���܂��B
        // 6/26�C��St����������������
        condition.setJinjiCd(pagePc.getKyoinCd());
        // 6/26�C��En����������������

        //�ǂ݉����敪��ݒ�
        condition.setYomiganaKbn(pc.getYomiganaKbn());
        
// 2008-02-08 UPEX-250 START
        //�w�ȕ\���敪��ݒ�
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
     * �w�肵�������̏o�ȗ����擾���܂��B <br>
     * 
     * @param pagePc
     *            Kmc00201A�y�[�W�R�[�h
     * @param pc
     *            Kmc00202A�y�[�W�R�[�h
     * @param jugyoRisyuDTO
     *            �N�x�w���c�s�n�N���X
     * @return �o�ȗ�
     */
    private String getAcquirShussekiRitsu(SeisekiService seisekiService,
            Kmc00201A pagePc, Kmc00202A pc, JugyoRisyuDTO jugyoRisyuDTO) {
        //�����������Z�b�g
        ShussekiRitsuCondition shussekiRitsuCondition = new ShussekiRitsuCondition();
        shussekiRitsuCondition.setNendo(Integer.parseInt(pagePc.getPropNendo()
                .getStringValue()));
        shussekiRitsuCondition.setGakkiNo(Integer.parseInt(pagePc
                .getPropGakkiNo().getStringValue()));
        // 6/21 �o�O�C���̈׃R�����g������������
        //		shussekiRitsuCondition.setJugyoCd(
        //				pc.getPropKamokumei().getStringValue());
        // 6/21 �o�O�C���̈׃R�����g������������
        // 6/21 �o�O�C���̈גǋL����������
        shussekiRitsuCondition.setJugyoCd(pc.getJugyoCd());
        // 6/21 �o�O�C���̈גǋL����������

        shussekiRitsuCondition.setKanriNo(jugyoRisyuDTO.getKanriBangou()
                .longValue());
        // ����ɃV�X�e���̌��ݓ��t��ݒ�
        // �u4�F"yyyy-M-d"�v���w��
        shussekiRitsuCondition.setKijunbi(DateFactory.getInstance());
        //�o�ȗ����擾
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
     * �w�肳�ꂽ���Ƃɏo�������ݒ肳��Ă��邩���肵�܂��B <br>
     * 
     * @param pagePc
     *            Kmc00201A�y�[�W�R�[�h
     * @param pc
     *            Kmc00202A�y�[�W�R�[�h
     * @return �o�����̃t���O
     */
    private boolean getAcquireShukketsuKanriBoolean(Kmc00201A pagePc,
            Kmc00202A pc) {

        JugyoService jugyoService = new JugyoService(this);
        //�����������Z�b�g
        ShukketsuSetteiCondition shukketsuSetteiCondition = new ShukketsuSetteiCondition();
        shukketsuSetteiCondition.setKaikoNendo(Integer.valueOf(pagePc
                .getPropNendo().getStringValue()));
        shukketsuSetteiCondition.setGakkiNo(Integer.valueOf(pagePc
                .getPropGakkiNo().getStringValue()));
        shukketsuSetteiCondition.setJugyoCode(pc.getJugyoCd());
        boolean flag = false;
        try {
            //�o����flag���擾
            flag = jugyoService
                    .acquireShukketsuKanriBoolean(shukketsuSetteiCondition);
        } catch (NoSuchDataException e) {
            UtilLog.error(this.getClass(), e);
            throw new GakuenSystemException(e);
        }

        return flag;
    }

    /**
     * ���Ƃ��Ƃ̊w���̑f�_���擾���܂��B <br>
     * 
     * @param pagePc
     *            Kmc00201A�y�[�W�R�[�h
     * @param pc
     *            Kmc00202A�y�[�W�R�[�h
     * @param jugyoRisyuDTO
     *            �N�x�w���c�s�n�N���X
     * @return �f�_��String
     */
    private String getSoten(SaitenService saitenService, Kmc00201A pagePc,
            Kmc00202A pc, JugyoRisyuDTO jugyoRisyuDTO) {

        String soten = null;
        //�����������Z�b�g
        HyokaCondition condition = new HyokaCondition();
        condition.setKaikoNendo(Integer.valueOf(pagePc.getPropNendo()
                .getStringValue()));
        condition.setGakkiNo(Integer.valueOf(pagePc.getPropGakkiNo()
                .getStringValue()));
        condition.setJugyoCd(pc.getJugyoCd());
        condition.setKanriNo(jugyoRisyuDTO.getKanriBangou());
        condition.setShikenKbn(String.valueOf(pagePc.getShikenFlg()));
        //�����敪�͒������
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
     * �w���̈ٓ��󋵂��擾���܂��B <br>
     * 
     * @param pagePc
     *            Kmc00201A�y�[�W�R�[�h
     * @param jugyoRisyuDTO
     *            �N�x�w���c�s�n�N���X
     * @return �ٓ��敪����
     */
    private String getAcquireGakuseiIdoState(GakuseiService gakuseiService,
            Kmc00201A pagePc, JugyoRisyuDTO jugyoRisyuDTO) {
        //�����������Z�b�g
        GakuseiIdoCondition gakuseiIdoCondition = new GakuseiIdoCondition();
        gakuseiIdoCondition.setKanriNo(jugyoRisyuDTO.getKanriBangou());
        gakuseiIdoCondition.setKaikoNendo(Integer.valueOf(pagePc.getPropNendo()
                .getStringValue()));
        gakuseiIdoCondition.setKaikoGakkiNo(Integer.valueOf(pagePc
                .getPropGakkiNo().getStringValue()));
        //�������
        if (pagePc.getShikenFlg() == ShikenUPKbn.TEIKISHIKEN.getCode()) {
            // 6/25�C����������������
            //			gakuseiIdoCondition.setShikenKbn("1");
            gakuseiIdoCondition.setShikenKbn(String
                    .valueOf(ShikenUPKbn.TEIKISHIKEN.getCode()));
            // 6/25�C��������������
            //�ǎ���
        } else if (pagePc.getShikenFlg() == ShikenUPKbn.TSUISHIKEN.getCode()) {
            // 6/25�C����������������
            //			gakuseiIdoCondition.setShikenKbn("2");
            gakuseiIdoCondition.setShikenKbn(String
                    .valueOf(ShikenUPKbn.TSUISHIKEN.getCode()));
            // 6/25�C��������������
            //�Ď���
        } else {
            // 6/25�C����������������
            //			gakuseiIdoCondition.setShikenKbn("3");
            gakuseiIdoCondition.setShikenKbn(String
                    .valueOf(ShikenUPKbn.SAISHIKEN.getCode()));
            // 6/25�C��������������
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
     * �ꗗ�̌������擾���܂��B <br>
     * 
     * @param pc
     *            Kmc00202A�̃y�[�W�R�[�h
     * @return �ꗗ�̌���
     */
    private int getRow(Kmc00202A pc) {

        RowCountCondition condition = new RowCountCondition();
        // ���ށi����͌Œ�j
        // �p�����[�^��Ēu�� 2006.06.24 Horiguchi Start
        //		condition.setBunrui("ROW_COUNT");
        condition.setBunrui("KMC");
        // �p�����[�^��Ēu�� 2006.06.24 Horiguchi End

        // ���ID
        condition.setKoumoku(pc.getFormId().toUpperCase());
        // �p�����[�^��Ēu�� 2006.06.24 Horiguchi Start
        //		condition.setKoumoku(pc.getFormId().toUpperCase());
        condition.setKoumoku("KMC00202A2");
        // �p�����[�^��Ēu�� 2006.06.24 Horiguchi End
        // �}��
        // �p�����[�^��Ēu�� 2006.06.24 Horiguchi Start
        //		condition.setSeqNo(new Integer("2"));
        condition.setSeqNo(new Integer("0"));
        // �p�����[�^��Ēu�� 2006.06.24 Horiguchi End

        // �ݒ�l�T�[�r�X����ꗗ�̌������擾���āA�\���s���ݒ�����{����
        SettingValueService setVal = new SettingValueService(this);
        int rowCnt = 0;
        try {
            rowCnt = setVal.acquireTableRowCount(condition);
        } catch (NoSuchDataException notE) {
            // �Y���f�[�^�����݂��Ȃ��ꍇ�́A�����l�u0�v��ݒ肷��B
            rowCnt = 0;
        }
        return rowCnt;
    }

    /**
     * �I�v�V�����e�[�u���Ɍ���������ێ����܂��B <br>
     * 
     * @param pc
     *            Kmc00202A�̃y�[�W�R�[�h
     */
      protected void saveDefaultItemValue(Kmc00202A pc) {
        String loginId = UtilUpSystem.getUpSystemData().getLoginUserBean()
                .getLoginId();
        UtilCosOpt utilOpt = new UtilCosOpt(getDbs(), loginId, pc.getFormId());
        utilOpt.preLoad(); // �f�[�^���ǂ݂���DAO�̃��R�[�h�L���b�V���Ɋi�[
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
     * �R���{�{�b�N�X�����������܂��B <br>
     * 
     * @param pc
     *            Kmc00202A�̃y�[�W�R�[�h
     */
    private void narabijunBind(Kmc00202A pc) {
        pc.getPropRow().getList().clear();
        pc.getPropDisplay().getList().clear();
        // �S���\��
        pc.getPropDisplay().addListItem("1",
                UtilProperty.getMsgItemString("KM_AllDisplay"));
        // �ō��w�N�̊w���̂�
        pc.getPropDisplay().addListItem("2",
                UtilProperty.getMsgItemString("KM_HighestNomiDisplay"));
        // �ō��w�N�̊w���ȊO
        pc.getPropDisplay().addListItem("3",
                UtilProperty.getMsgItemString("KM_HighestIgaiDisplay"));
        pc.getPropDisplay().setStringValue("1");
        // �w�Дԍ���
        pc.getPropRow().addListItem("1",
                UtilProperty.getMsgItemString("KM_GakusekiCdSort"));

        String yomiganaKbn = acquireYomiGanaKbn();
        if (yomiganaKbn.equals(YomiganaKbn.KANASHIMEI.getCode())) {
            // �J�i������
            pc.getPropRow().addListItem("2",
                    UtilProperty.getMsgItemString("KM_KanaSimeiSort"));
        } else if (yomiganaKbn.equals(YomiganaKbn.EIGOSHIMEI.getCode())) {
            // �p�ꎁ����
            pc.getPropRow().addListItem("2",
                    UtilProperty.getMsgItemString("KM_EnglishSimeiSort"));
        }
        pc.setYomiganaKbn(yomiganaKbn);

        // �w�ȑg�D�A�w�N�i�傫�����j
        pc.getPropRow().addListItem("3",
                UtilProperty.getMsgItemString("KM_GakkaDesc"));
        // �w�ȑg�D�A�w�N�i���������j
        pc.getPropRow().addListItem("4",
                UtilProperty.getMsgItemString("KM_GakkaAsc"));
        // �w�N�i�傫�����j
        pc.getPropRow().addListItem("5",
                UtilProperty.getMsgItemString("KM_GakunenDesc"));
        // �w�N�i���������j
        pc.getPropRow().addListItem("6",
                UtilProperty.getMsgItemString("KM_GakunenAsc"));
        String loginId = UtilUpSystem.getUpSystemData().getLoginUserBean()
                .getLoginId();
        
        // �w�N�i���������j�A�݂Ȃ����w�N�x�w���i�傫�����j�A�w�Дԍ����i���������j
        pc.getPropRow().addListItem("7",
                UtilProperty.getMsgItemString("KM_GakunenNendoGakkiGakusekiCdSort"));
        if (semesterDsp) {
        	// �w�ȑg�D�A�Z���X�^�i�傫�����j
	        pc.getPropRow().addListItem("8",
	                UtilProperty.getMsgItemString("KM_GakkaSemesterDescSort"));
	        // �w�ȑg�D�A�Z���X�^�i���������j
	        pc.getPropRow().addListItem("9",
	                UtilProperty.getMsgItemString("KM_GakkaSemesterAscSort"));
	        // �Z���X�^�i�傫�����j
	        pc.getPropRow().addListItem("10",
	                UtilProperty.getMsgItemString("KM_SemesterDescSort"));
	        // �Z���X�^�i���������j
	        pc.getPropRow().addListItem("11",
	                UtilProperty.getMsgItemString("KM_SemesterAscSort"));
	        // �Z���X�^�i���������j�A�݂Ȃ����w�N�x�w���i�傫�����j�A�w�Дԍ����i���������j
	        pc.getPropRow().addListItem("12",
	                UtilProperty.getMsgItemString("KM_SemesterNendoGakkiGakusekiCdSort"));
        }
        
        // �]������
        pc.getPropHyokaWariai().getList().clear();
        // �]���R�[�h��
        pc.getPropHyokaWariai().addListItem("1",
                UtilProperty.getMsgItemString("KM_HyokaCdJoken"));
        // �]����A�]���R�[�h��
        pc.getPropHyokaWariai().addListItem("2",
                UtilProperty.getMsgItemString("KM_HyokaKijunHyokaCdJoken"));

        // �]�������o�̓`�F�b�N
        pc.getPropHyokaWariaiChk().setChecked(false);
        
        // �]���������
        pc.getPropHyokaWariaiPrt().getList().clear();
        // �]���R�[�h��
        pc.getPropHyokaWariaiPrt().addListItem("1",
                UtilProperty.getMsgItemString("KM_HyokaCdJoken"));
        // �]����A�]���R�[�h��
        pc.getPropHyokaWariaiPrt().addListItem("2",
                UtilProperty.getMsgItemString("KM_HyokaKijunHyokaCdJoken"));
        
        UtilCosOpt utilOpt = new UtilCosOpt(getDbs(), loginId, pc.getFormId());
        utilOpt.preLoad(); // �f�[�^���ǂ݂���DAO�̃��R�[�h�L���b�V���Ɋi�[
        // �����ł͔N�x��ݒ肵�Ă��܂��B
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
     * @return �ǂ݉����敪
     */
    private String acquireYomiGanaKbn() {

        String yomiganaKbn = null;
        // �����t�������擾
        // �l��DAO
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
     * �w�ȕ\���敪���擾����
     * <P>
     * �p�����[�^���w�ȕ\���敪���擾���܂��i0:�����w�� 1:�J���L�������w�ȁj
     * 
     * @param pc Kmc00202A
     * @return �g�����U�N�V���������̌���
     */
    private void setDspGakka(Kmc00202A pc){
		// �p�����[�^DAO
		CouParamDAO paramDAO = 
			(CouParamDAO) getDbs().getDao(CouParamDAO.class);		
		// �\������w�Ȃ��擾
		CouParamAR dspGakka = null;
		try {
			dspGakka = paramDAO.findByPrimaryKey("KMC","GAKKA_NAME",0);
			
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		}
		// �y�[�W�R�[�h�֐ݒ�
		if (dspGakka != null) {
			pc.setDspGakka(dspGakka.getValue());
		} else {
			// �����w�ȑg�D���f�t�H���g�Ƃ���B
			pc.setDspGakka("0");
		}
    }
// 2008-02-08 UPEX-250 END    

    /**
     * ���v�O���t�\���敪���擾����B
     * �p�����[�^��葍�v�O���t�\���敪���擾���܂��i0:�\�����Ȃ� 1:�\������j
     * 
     * @param pc Kmc00202A
     */
    private void setSokeiGraphFlag(Kmc00202A pc){
		// �p�����[�^DAO
		CouParamDAO paramDAO = (CouParamDAO) getDbs().getDao(CouParamDAO.class);		
		// ���v�O���t�\���敪���擾
		CouParamAR couParamAR = null;
		try {
		    couParamAR = paramDAO.findByPrimaryKey("KMC","DSP_SOKEI",0);
			
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		}
		// �y�[�W�R�[�h�֐ݒ�
		if (couParamAR != null && "0".equals(couParamAR.getValue())) {
			pc.setSokeiGraphFlg(false);					// ���v�O���t��\��
			pc.getPropHyokaWariai().setValue("2");		// �]���������u�]����A�]���R�[�h���v
			pc.getPropHyokaWariaiPrt().setValue("2");
		} else {
			pc.setSokeiGraphFlg(true);
		}
    }

    /**
     * �o�^�������s���܂��B
     * <P>
     * �o�^�A�X�V��ʂ��ꗗ��ʂɖ߂��܂��B
     * 
     * @param pagecode
     *            �y�[�W�R�[�h
     * @return �g�����U�N�V���������̌���
     */
    protected String update(PageCodeBaseEx pagecode) {
        final Kmc00202A pc = (Kmc00202A) pagecode;
        Kmc00201A pagePc = (Kmc00201A) UtilSystem
                .getManagedBean(Kmc00201A.class);

        // ��ʂ̉B�����ځi�N�x�A�w���A���ƃR�[�h�A�����敪�A�����񐔂̌���������j���擾
        String browserHidden = pc.getPropBrowserHidden().getStringValue();
        // �T�[�o�̍��ځi�N�x�A�w���A���ƃR�[�h�A�����敪�A�����񐔂̌���������j���擾
        String pcItem = pc.getPropNendo().getStringValue() + "|" +  pc.getPropGakki().getStringValue() 
							+ "|" +  pc.getJugyoCd() + "|" +  pc.getPropShikenKubun().getStringValue() 
							+ "|" +  UtilStr.cnvNull(pc.getShikenKaisu());
        // ������ʂ̎g�p�ɂ��A��ʂƃT�[�o�̓��e���قȂ��Ă��Ȃ������`�F�b�N
        if (!pcItem.equals(browserHidden)) {
            UtilSystem.getDisplayInfo().setDisplayMessage(UtilUpMsg.editMsg(
            				UtilProperty.getMsgString(CoMsgConst.CO_MSG_0060E)));
            pc.setContentRendered(false);
            return UpActionConst.RET_FALSE;
        }
        
        // �m��{�^���������̃y�[�W��\��
        pc.getPropSaitenTorokuTable().setDispPage(
        		pc.getPropSaitenTorokuTable().getRowNumber());
        
        if (pc instanceof Jgc91102A) {
       	 pagePc = (Kmc00201A) UtilSystem
            .getManagedBean(Jgc91101A.class);
        }
        final StringBuffer rowClass = new StringBuffer();
        final SaitenService saitenService = new SaitenService(this);
        final JugyoService jugyoService = new JugyoService(this);

        //��s�Ή�
        final List dataList = pc.getPropSaitenTorokuTable().getNoEmptyList();

        // �]�����̍ăZ�b�g
        resethyokaName(pc);
		
        int rowCnt = getRow(pc);

        int errCount = 0;
        //		boolean updateFlg = false;

    	// ���̓_�ғo�^�`�F�b�N
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
					
				// ���̓_�҂����݂���ꍇ
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
        //��ʂ̃f�[�^�[�����
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

            //��������ȊO�����񐔃Z�b�g
            if (pc.getShikenKaisu() != null) {
                hyokaKoshinValue.setSikenKaisu(Integer.valueOf(pc
                        .getShikenKaisu()));
            }
            hyokaKoshinValue.setSitnUpdateDate(DateFactory.getInstance());

            //�f�_�^�p�̏ꍇ
            hyokaKoshinValue.setSaitenKbn(String.valueOf(SaitenKbn.SOTEN
                    .getCode()));

            try {
                // �`�F�b�N�̃��b�Z�[�W
                String message = "";

                //�f�_�̓��̓`�F�b�N���s��
                message = acquireCheckMsg(jugyoService, listBean,
                        saitenService, pagePc);

                listBean.setMessage(message);

                //�G���[�����J�E���g
                if (message.length() > 0) {
                    errCount++;
                }
                //		        if (listBean.getSoten() == null ||
                //						listBean.getSoten().trim().length() == 0) {
                //�G���[���b�Z�[�W���Ȃ��A�܂��͑f�_�����͔͂w�i��
                if (message.length() != 0
                        || (listBean.getSoten() == null
                        || UtilStr.cnvTrim(listBean.getSoten()).length() == 0)) {

                    rowClass.append(", rowClass1");
                } else {
                    rowClass.append(", selectiveLine");
                }

                //�G���[�ł͖����ꍇ�͍X�V�������s��
                if (message.length() == 0) {

                    Integer soten;
                    if (listBean.getSoten() == null
                            || UtilStr.cnvTrim(listBean.getSoten()).equals("")) {
                        soten = null;
                    } else {
                    	
                        // �]���œ��͂��ꂽ�ꍇ
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
                        //��������̏ꍇ
                        saitenService.updateTeiki(hyokaKoshinValue);

                    } else {
                        //�ǍĎ����̏ꍇ
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
            // ���b�Z�[�W�u�o�^����܂����B�v
            UtilSystem.getDisplayInfo().setPopupMessage(
                    UtilProperty.getMsgString(SyMsgConst.SY_MSG_0001I));
            pc.getPropValueChanged().setValue(Boolean.FALSE);
            pc.getPropHasInputError().setValue(Boolean.FALSE);
        }
        /// add 2006-07-02
        else {
            // ���b�Z�[�W�u�G���[������܂��̂Ŋm�F���Ă��������v
            UtilSystem.getDisplayInfo().setDisplayMessage(
                    UtilUpMsg.editMsg(UtilProperty
                            .getMsgString("KMC_MSG_0009E")));
            pc.getPropValueChanged().setValue(Boolean.TRUE);
            pc.getPropHasInputError().setValue(Boolean.TRUE);
        }
        /// add 2006-07-02 end

        // �ꗗ�ɋ�s���ł���ꍇ ��s�ɃX�^�C����ݒ�
        if (dataList.size() % rowCnt > 0) {
            for (int i = 0; i < rowCnt - (dataList.size() % rowCnt); i++) {
                rowClass.append(",rowClass1");
            }
        }

        //�̓_�o�^�ς݂̊w���́A�s���O���[�ɂ���B
        final String rowClasses = rowClass.substring(1);
        pc.getPropSaitenTorokuTable().setRowClasses(rowClasses);
        pc.getPropSaitenTorokuTable().setStockRowClasses(rowClasses);

        // �X�^�C���̐ݒ�
        pc.getPropSaitenTorokuTable().editPageRowClass(
                pc.getPropSaitenTorokuTable().getRowNumber()
                        / pc.getPropSaitenTorokuTable().getRows());
        
// UPEX-1290�@�Ή��@2010/06/12 k.yokoi start
//  �G���[�����݂��Ă��Ă��A����o�^�f�[�^�̓R�~�b�g
//        //		return UpActionConst.RET_TRUE;
//        // add 2006-07-02
//        //�G���[��1���ł�����ꍇ�̓R�~�b�g���Ȃ�
//        if (errCount == 0) {
        //		return UpActionConst.RET_TRUE;
//        } else {
//            return UpActionConst.RET_FALSE;
//        }
//        // add 2006-07-02 end
            return UpActionConst.RET_TRUE;
// UPEX-1290�@�Ή��@2010/06/12 k.yokoi end
    }

    /**
     * ��ʂ̕]�����̂ɖ��̂��Z�b�g���܂��B
     * 
     * @param pagecode
     *            �y�[�W�R�[�h
     */
    protected void resethyokaName(PageCodeBaseEx pagecode) {

        final Kmc00202A pc = (Kmc00202A) pagecode;
        Kmc00201A pagePc = (Kmc00201A) UtilSystem
                .getManagedBean(Kmc00201A.class);
        if (pc instanceof Jgc91102A) {
       	 pagePc = (Kmc00201A) UtilSystem
            .getManagedBean(Jgc91101A.class);
        }
    	
        //��s�Ή�
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

// 2007/02/27 �s��Ǘ��ꗗ�FNo.3621 Start -->>
//            // �G���[���b�Z�[�W�\���p
//            int errMsgSotenToMax = 0;
//            //�ǎ��A�Ď��ȊO
//            if (!(pagePc.getShikenFlg() == ShikenUPKbn.TEIKISHIKEN.getCode())) {
//                ShikenBetuHyokaKijunCondition condition = new ShikenBetuHyokaKijunCondition();
//                condition.setKanriNo(listBean.getKanriNo());
//                condition.setShikenKbn(String.valueOf(pagePc.getShikenFlg()));
//                try {
//                    errMsgSotenToMax = saitenService
//                            .acquireShikenBetuSotenMax(condition);
//                } catch (NoSuchDataException e) {
//                    //�X���[
//                }
//            }
// <<-- End   2007/02/27 �s��Ǘ��ꗗ�FNo.3621

            // ��ʓ��͑f�_�ޔ�p
            String sotenStringWk = sotenString;
            String soten = "";
            
            // �]���œ��͂��ꂽ�ꍇ
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
                	
            		
            		// ���͒l�������̏ꍇ
	            	if (UtilStr.cnvNull(soten).equals("")
	            	 || Integer.valueOf(soten).intValue() <= 100) {
	            		// �]���œ��͂��ꂽ�ꍇ�A�]����ɑ��݂��Ȃ��]���R�[�h����
	            		// �f�_�͈͂�100�_�ȉ��ɊY������]���R�[�h�̏ꍇ�̓G���[
	                    message = UtilProperty.getMsgString(KmMsgConst.KMC_MSG_0007E);
	                    return message;
	            	} else {
	            		sotenString = soten;
	                    listBean.setSoten(sotenString);
	            	}
            	} else {
            		// ���͒l�����l�̏ꍇ
	            	if (!UtilStr.cnvNull(sotenString).equals("")
   	            	 && Integer.valueOf(sotenString).intValue() >= 101) {
   	            		// ���l�œ��͂��ꂽ�ꍇ�A�]����ɑ��݂��Ȃ��]���R�[�h����
   	            		// �f�_�͈͂�101�_�ȏ�ɊY������]���R�[�h�̏ꍇ�̓G���[
	                    message = UtilProperty.getMsgString(KmMsgConst.KMC_MSG_0025E);
	                    return message;
	            	} else {
	                    listBean.setSoten(sotenString);
	            	}
            	}
            }
            
            if (UtilStr.chkHankakuNumberMoji(sotenString) == false) {
                //���͑f�_�͐������Ȃ�
                message = UtilProperty.getMsgString(SyMsgConst.SY_MSG_0160E,
                        SaitenKbn.SOTEN.getName());
                return message;
            }

            //		    if (Integer.parseInt(sotenString) < 0 ||
            //					Integer.parseInt(sotenString) > 100) {
            if (Integer.parseInt(sotenString) < 0) {
                //�f�_�ɐ��l�͈̔͂�0�`100�ɂ��Ȃ�
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

// 2007/02/27 �s��Ǘ��ꗗ�FNo.3621 Start -->>
//            // 6/29�C��St����������������
//            //�ǍĎ������̂݃`�F�b�N
//            if (!checkMaxSoten(saitenService, listBean.getKanriNo(), pagePc
//                    .getShikenFlg(), listBean.getSoten())) {
//                //�f�_�̍ő�l�𒴂��Ă��܂��i�ő�l�F{0}�j�B
//                message = UtilProperty.getMsgString("KMC_MSG_0008E", String
//                        .valueOf(errMsgSotenToMax));
//                return message;
//            }
//            // 6/29�C��En����������������
// <<-- End   2007/02/27 �s��Ǘ��ꗗ�FNo.3621
            // 6/23�C��St����������������
            // ����������̂�
            if (!checkHyokaSoten(jugyoService, listBean.getNyugakNendoCur(),
                    listBean.getNyugakGakkiNoCur(), listBean
                            .getCurGakkaCd(), listBean.getSoten())) {

                message = UtilProperty.getMsgString(KmMsgConst.KMC_MSG_0006E);
                return message;
            }
            // 6/23�C��En������������

// 2007/02/27 �s��Ǘ��ꗗ�FNo.3621 Start -->>
			try {
	            //�ǍĎ������̂݃`�F�b�N
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
// <<-- End   2007/02/27 �s��Ǘ��ꗗ�FNo.3621

            // ��ʓ��͑f�_�ޔ�p��߂�
            listBean.setSoten(sotenStringWk);
        }
        return message;
    }

    // 6/23�C��St����������������
    /**
     * 
     * �G���[����i�f�_�͈̓`�F�b�N�j
     * 
     * @param service
     *            ���ƃT�[�r�X
     * @param nnc
     *            ���w�N�x
     * @param ngnc
     *            ���w�w��No
     * @param sg
     *            �f�_
     * @param st
     *            �J���L�������w�ȑg�D�R�[�h
     * @return service.checkHyokaSoten()�߂�l
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

    // 6/23�C��En����������������

    /**
     * @param kmc00201A
     * @param kmc00202A
     * @return
     */
    private JogaiTaishoIdoKubunCondition makeJogaiTaishoIdoKubunCondition(
            Kmc00201A kmc00201A, Kmc00202A kmc00202A) {
        final JogaiTaishoIdoKubunCondition condition = new JogaiTaishoIdoKubunCondition();

        // ���ƃR�[�h
        condition.setJugyoCode(kmc00202A.getJugyoCd());

        // �J�u�N�x
        String str = kmc00201A.getPropNendo().getStringValue();
        if (str != null) {
            condition.setKaikoNendo(Integer.parseInt(str));
        } else {
            UtilLog.warn(getClass(), "�J�u�N�x���ݒ肳��Ă��܂���B");
        }

        // �w��NO
        str = kmc00201A.getPropGakkiNo().getStringValue();
        if (str != null) {
            condition.setGakkiNo(Integer.parseInt(str));
        } else {
            UtilLog.warn(getClass(), "�w��NO���ݒ肳��Ă��܂���B");
        }

        // �����敪
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
        
        // 6/23�C��St����������������
        bean.setShozokuGakkaCd(dto.getShozokuGakkaCd());
        bean.setCurGakkaCd(dto.getCurGakkaCd());
        bean.setNyugakNendoCur(dto.getNyugakNendoCur());
        bean.setNyugakGakkiNoCur(dto.getNyugakGakkiNoCur());
        // 6/23�C��En����������������

//��Q�Ή� UPEX-153 �̓_�o�^��ʂ̏o���������ƒP�ʂŕ\������׏C�� 2009.02.23 Satomi Start        
//��QNO4082 �o���p�t�H�[�}���X�A�b�v���ʉ� 2007.08.20 Horiguchi Start
//        // �o�ȗ��̐ݒ�
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
        // �o�ȗ��̐ݒ�
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

        // �o�ȗ��̐ݒ�
        if( SyuYaKuMap != null && !SyuYaKuMap.isEmpty() ){
            
        	String shussekiRitsu = null;
            ShukkessekiDTO sskDto = null;
            
            Long kanriNo = dto.getKanriBangou();
            
            // �W��}�b�v��Key�ɑΏۂ̊Ǘ��ԍ������݂��邩�`�F�b�N
            if( SyuYaKuMap.containsKey( kanriNo ) ){
            	
            	sskDto = (ShukkessekiDTO)SyuYaKuMap.get( kanriNo );
            	
            	float kessekiSu = Float.parseFloat( String.valueOf( sskDto.getKessekiKaisu()) );	// ���ȉ񐔂̎擾
            	float jugyokaiSu = Float.parseFloat( String.valueOf( sskDto.getJugyoKaisu()) );		// ���Ɖ񐔂̎擾
            	float syussekiSu = jugyokaiSu - kessekiSu;		// �o�ȉ񐔂̎擾
            	
            	// �o�ȗ��̌v�Z���l�̌ܓ�����悤�ɏC��
            	if ( jugyokaiSu != 0 && syussekiSu != 0){
            		
            		BigDecimal shussekiPercent = 
            			new BigDecimal(String.valueOf((float)(syussekiSu * 100) / jugyokaiSu));
		    	
            		// �o�ȗ����擾
            		shussekiRitsu = "" + shussekiPercent.setScale(0,BigDecimal.ROUND_HALF_UP);
            	}else{
	        		//�o�ȗ���[0]�Őݒ�
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
//��QNO4082 �o���p�t�H�[�}���X�A�b�v���ʉ� 2007.08.20 Horiguchi End
//��Q�Ή� UPEX-153 �̓_�o�^��ʂ̏o���������ƒP�ʂŕ\������׏C�� 2009.02.23 Satomi End        
        bean.setSoten(getSoten(saitenService, kmc00201A, kmc00202A, dto));

        // �`�F�b�N�̃��b�Z�[�W
        String message = "";
        boolean rowClassFlag = true;
        if (bean.getSoten() != null && bean.getSoten().length() > 0) {
            final String sotenString = UtilStr.cnvTrim(bean.getSoten());
            if(UtilStr.chkHankakuNumberMoji(sotenString) == false){  	
//            	���͑f�_�͐������Ȃ�
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

                //�f�_�ɐ��l�͈̔͂�0�ȉ��ɂ��Ȃ�
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

   		// �]�����Z�b�g
        bean.setHyokaMeisho(cnvHyokaRyak(dto.getNyugakNendoCur().intValue(),
				   dto.getNyugakGakkiNoCur().intValue(),
				   dto.getCurGakkaCd(),
				   bean.getSoten()));

        // �]���Ƃ��ēo�^�ł���101�_�ȏ�̏ꍇ�̂ݕ]���_�ɊY������]���R�[�h���Z�b�g����
   		bean.setSoten(cnvHyokaCd(dto.getNyugakNendoCur().intValue(),
								   dto.getNyugakGakkiNoCur().intValue(),
								   dto.getCurGakkaCd(),
								   bean.getSoten()));

        
        //�ٓ��敪���X�g�Ɋ܂܂��ꍇ,���Y�ٓ��敪�̖��̂�\������B
        if (bean.getBiko() == null) {
            bean.setMessage(message);
        } else {
            bean.setMessage("<br>" + message);
        }

        //�̓_�o�^�ς݂̊w���́A�s���O���[�ɂ���B
        if (rowClassFlag) {
            sb.append(", rowClass1");
        } else {
            sb.append(", selectiveLine");
        }

        return bean;
    }
    
    /**
     * �Ǘ��ԍ����Z���X�^���擾���܂�
     * 
     * @param kainriNo
     * @return String
     */
    protected String getSemester(long kanriNo) {
    	
    	String ret = "";
    	
    	try {
    		// ���ݓ�
    		java.sql.Date currentDate =
    			UtilDate.cnvSqlDate(
    					UtilDate.parseDate(UtilDate.getDateSystem()));

    		// �w��
    		CobGaksekiDAO cobGaksekiDAO = (CobGaksekiDAO) getDbs()
    				.getDao(CobGaksekiDAO.class);
    		CobGaksekiAR cobGaksekiAR = null;

    		// ���ݓ��ɂčŐV�̗L���Ȋw�Ђ��擾����
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
     * �p�����[�^�̐ݒ�l���f�_�Ƃ��ēo�^���ł��f�_��101�_�ȏ�̏ꍇ�ɑf�_��]���R�[�h�ɕϊ����܂�
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
    		//���͑f�_�͐������Ȃ�
    		String str= cnvSoten(nyugakNendoCur,nyugakGakkiNoCur,curGakkaCd,soten);
    		//�]���R�[�h������܂���
    		if("".equals(str)){
    			return soten;
    		}
    		//�]���R�[�h�̑f�_�͑嘰100
    		if(str.length()>1){
    			if(sotenAtukai.equals(HYOKA)){
    				return soten;
    			} else{
    				return str.substring(1,str.length());
    			}	
    		}
    		//���ʂ̕]���R�[�h
    		return str;
    		
    	}else {
    		intSoten = Integer.valueOf(soten).intValue();
    	}
    	
		try {
			//���͑f�_�͐���������܂�
			if (sotenAtukai.equals(SOTEN)) {
				return soten;
			} else if (sotenAtukai.equals(HYOKA)) {
				intSoten = Integer.valueOf(soten).intValue();
				if (intSoten < 101) {
					return soten;
				} else {
					// �]����z��
					KmbHykHaiUPDAO kmbHykHaiUPDAO = (KmbHykHaiUPDAO) getDbs().getDao(KmbHykHaiUPDAO.class);
					KmbHykHaiAR kmbHykHaiAR;

					// �]���
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
     * �f�_��]�����̂ɕϊ����܂�
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
//    		���͑f�_�͐������Ȃ�
    		String str = cnvSoten(nyugakNendoCur,nyugakGakkiNoCur,curGakkaCd,soten);
    		//���͑f�_������܂�
    		if(!"".equals(str.trim())){
//    			���͑f�_�͑嘰100
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
			// �]����z��
			KmbHykHaiUPDAO kmbHykHaiUPDAO = (KmbHykHaiUPDAO) getDbs().getDao(KmbHykHaiUPDAO.class);
			KmbHykHaiAR kmbHykHaiAR;

			// �]���
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
     * �]���R�[�h��f�_�ɕϊ����܂�
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
			// �]����z��
			KmbHykHaiUPDAO kmbHykHaiUPDAO = (KmbHykHaiUPDAO) getDbs().getDao(KmbHykHaiUPDAO.class);
			KmbHykHaiAR kmbHykHaiAR;

			// �]���
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
						//�f�[�^�x�[�X�ł͕]��������܂�
						if(hyoka.equals(kmzHykAR.getHyokaCd()) ){
							//�]���̑f�_�嘰100�A�]��+�f�_��Ԏ�
							if(kmzHykAR.getSotenFrom().intValue() >100){
									return hyoka + String.valueOf(kmzHykAR.getSotenTo());			
							}	
							//�]���̑f�_����100,�]����Ԏ�
								return hyoka;
						}	
					}
					//�f�[�^�x�[�X�ł͕]��������܂���
					return "";
				}
			}

		} catch (DbException e) {
			throw new RuntimeException(e);
		}	        

    	return ret;
    }
    
    /**
     * ��ʍĕ`��p�ɕ]���R�[�h�܂��͑f�_�ɑΉ�����]�����̂ɕϊ����܂�
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
			// �]����z��
			KmbHykHaiUPDAO kmbHykHaiUPDAO = (KmbHykHaiUPDAO) getDbs().getDao(KmbHykHaiUPDAO.class);
			KmbHykHaiAR kmbHykHaiAR;

			// �]���
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
						// 101�_�ȏ�̏ꍇ�͕ϊ����Ȃ�
						return ret;
					}
				} else {
					kbn = 2;
				}
			}
			if (kbn == 1) {
				// �p�����[�^��0:�f�_�Ƃ��ēo�^�̏ꍇ�Ő��l�œ��͂���Ă���ꍇ�A
				// �܂��̓p�����[�^��1:�]���Ƃ��ēo�^�̏ꍇ�Ő��l�œ��͂���Ă���ꍇ
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
				// �p�����[�^��1:�]���Ƃ��ēo�^�̏ꍇ�ŕ����œ��͂���Ă���ꍇ
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
						// �f�_FROM��101�_�ȏ�̏ꍇ�̂ݕ]�����̂�ݒ肷��
						ret = isDspHyoka ? kmzHykAR.getHyokaCd() : 
							UtilStr.cnvNull(kmzHykAR.getHyokaNameRyak());
					}
				}
			}
		} catch (DbException e) {
			UtilLog.error(this.getClass(),e);
			//��O�������͏������Ȃ�
			return null;
		}	        
    	return ret;
    }
    
    /**
     * �q��ʃI�[�v���󂯎�胁�\�b�h
     * 
     * @param pagecode
     * @return
     */
    protected String detail(PageCodeBaseEx pagecode) {

        Kmc00202A pc = (Kmc00202A) pagecode;

        // �]�����̍ăZ�b�g
        resethyokaName(pc);
        
        Kmc00202AL01Bean listBean = (Kmc00202AL01Bean) pc
                .getPropSaitenTorokuTable().getRowData();

// UPEX-1237 2009/11/09 y-matsuda upd Start
    	// �l���R�[�h���Í���
		UtilCrypt utilCrypt  = new UtilCrypt();
		String kanriNo = utilCrypt.encryptToStr(String.valueOf(listBean.getKanriNo()));

    	// JSP��hidden�֊Ǘ��ԍ����Z�b�g
        pc.getPropKanriNoHidden().setStringValue(kanriNo);
//        pc.getPropKanriNoHidden().setStringValue(
//                String.valueOf(listBean.getKanriNo()));
// UPEX-1237 2009/11/09 y-matsuda upd End
        
        return UpActionConst.RET_TRUE;
    }

    // 6/29�C��St����������������
    /**
     * 
     * �G���[����i�f�_TO�`�F�b�N�j
     * 
     * @param service
     *            �̓_�T�[�r�X
     * @param kanriNo
     *            �Ǘ��ԍ�
     * @param shikenFlg
     *            �����敪
     * @param soten
     *            �f�_
     * @return hantei �f�_TO�̔��茋�ʁitrue:OK�Afalse�FNG�j
     */
    private boolean checkMaxSoten(SaitenService service, Long kanriNo,
            int shikenFlg, String soten) {

        if (shikenFlg == ShikenUPKbn.TEIKISHIKEN.getCode()) {
            // ��������̏ꍇ�Atrue�ɂă`�F�b�N���s��Ȃ��B
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
// 2007/02/15 �s��Ǘ��ꗗ�FNo.3201 Start -->>
// �Ώۃf�[�^���P�����Ȃ��ꍇ�́A���̏������s����B
// �Ώۃf�[�^���P�����Ȃ��ꍇ�͐����`�F�b�N�������Ȃ��ׁA����OK�Ŗ߂��B
//            hantei = false;
            hantei = true;
// <<-- End   2007/02/15 �s��Ǘ��ꗗ�FNo.3201
        }
        return hantei;
    }
    // 6/29�C��En����������������
// ��Q�Ή� UPEX-153 �̓_�o�^��ʂŏo���������ƒP�ʂŕ\������גǉ� 2009.02.04 Takemoto add Start
    /**
     *
     * �o�����̏W�񏈗����s�� 
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
        		
				// �Ǘ��ԍ�(Key)�̑��݃`�F�b�N
				if( SyuYaKuMap.containsKey( newKanriNo )){
					
					// �Ǘ��ԍ�(Key)�����ɑ��݂����ꍇ�́A�e�񐔂̉��Z�������s��
					ShukkessekiDTO keyDto = (ShukkessekiDTO)SyuYaKuMap.get(newDto.getKanriNo());
					setAppendKaisu(keyDto,newDto);
					
				}else{
					
					// �Ǘ��ԍ�(Key)��������ΐV�K�o�^
					SyuYaKuMap.put(newDto.getKanriNo(),newDto);
				}
        	}
        }
        
    }
    	
	/**
	 * �o�����̊e�񐔂̉��Z�������s��(�o�������ƒP�ʗp�擾�p)
	 * 
	 * @param	keyDto	���Z��(Key)�̏o�����DTO
	 * 			newDto	�V�K�̏o�����DTO
	 * 
	 * @return List �p�����[�^���X�g
	 * @throws GakuenException
	 * @throws DbException
	 */
	public void setAppendKaisu(ShukkessekiDTO keyDto,ShukkessekiDTO newDto){
		
		// ===================================================================
		// ���Z��(Key)�̏o�����DTO�̊e�񐔂̎擾
		// ===================================================================
		int keyKessekiKaisu = keyDto.getKessekiKaisu(); 				// ���Z��DTO�F���ȉ񐔂��擾
		int keyJugyoKaisu = keyDto.getJugyoKaisu(); 					// ���Z��DTO�F���Ɖ񐔂��擾
		
		// ===================================================================
		// �V�K�̏o�����DTO�̊e�񐔂̎擾
		// ===================================================================
		int newKessekiKaisu = newDto.getKessekiKaisu(); 				// �V�KDTO�F���ȉ񐔂��擾
		int newJugyoKaisu = newDto.getJugyoKaisu(); 					// �V�KDTO�F���Ɖ񐔂��擾
		
		// ===================================================================
		// �e�񐔂����Z���ĉ��Z��(Key)�̏o�����AR�ɃZ�b�g����
		// ===================================================================
		keyDto.setKessekiKaisu( keyKessekiKaisu + newKessekiKaisu );	// ���ȉ񐔂����Z���Ċi�[
		keyDto.setJugyoKaisu( keyJugyoKaisu + newJugyoKaisu );			// ���Ɖ񐔂����Z���Ċi�[
		
	}
// ��Q�Ή� UPEX-153 �̓_�o�^��ʂŏo���������ƒP�ʂŕ\������גǉ� 2009.02.04 Takemoto add End 
}