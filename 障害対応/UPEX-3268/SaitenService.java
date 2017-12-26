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
 * �̓_�T�[�r�X <br>
 * �̓_�Ɋւ�����Ƒ����񋟂��܂��B
 * 
 * @author JApan System Techniques Co.,Ltd.
 */
public class SaitenService extends Service {

// 2007/02/27 �s��Ǘ��ꗗ�FNo.3621 Start -->>
	/** �f�_�i�ǍĎ��]�����~�b�g�`�F�b�N�p�j */
	public static final int SOTEN_FROM = 0;				// �f�_FROM
	public static final int SOTEN_TO   = 100;				// �f�_TO

	/** ���ʁi�ǍĎ��]�����~�b�g�`�F�b�N�p�j */
	public static final int MAX_SOTEN_CHK_NO_ERR = 0;		// �G���[�Ȃ�
	public static final int MAX_SOTEN_CHK_ERR_1  = 1;		// ���~�b�g���߂őf�_���ySOTEN_TO�z�ȉ�
	public static final int MAX_SOTEN_CHK_ERR_2  = 2;		// ���~�b�g���߂őf�_���ySOTEN_TO�z���傫��
// <<-- End   2007/02/27 �s��Ǘ��ꗗ�FNo.3621

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
	 * @param service �ʃC���X�^���X
	 */
	public SaitenService(Service service) {
		super(service);
	}

    /**
     * �w���̕]������擾����
     * ���Ƃ��Ƃ̊w���̕]������擾����(�]�����@���]�����̂̏ꍇ�̂�)
     * 
     * @param kanriNo �Ǘ��ԍ�
     * @return hyokaKijunList<HyokaKijun> �]������X�g
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    public List listHyokaKijun(String kanriNo)  
    	throws NoSuchDataException {
        // ���O�����`�F�b�N
        selfTestListHyokaKijunPre(kanriNo);
        // ��񐶐�
        List hyokaKijunList = listHyokaKijunMaker(kanriNo);
        // ��������`�F�b�N
        selfTestListHyokaKijunPost(hyokaKijunList);
        return hyokaKijunList;
    }

    /**
     * 
     * ��������`�F�b�N
     * 
     * @param hyokaKijunList �]������X�g
     */
    private void selfTestListHyokaKijunPost(List hyokaKijunList) {
    	
 
    	if (hyokaKijunList == null) {
    		throw new BusinessRuleException("�]������X�g�� null");
    	}		

    	int listSize = hyokaKijunList.size();
		if (listSize == 0) {
			throw new BusinessRuleException("�]������X�g�� 0��");
		}

		for (int i = 0; i < hyokaKijunList.size(); i++ ) {
			
			if (hyokaKijunList.get(i) == null) {
				throw new BusinessRuleException("�]������X�g�̗v�f�� null");
			}
			
			if (!(hyokaKijunList.get(i) instanceof HyokaKijunDTO) ) {
				throw new 
				BusinessRuleException("�]������X�g�̗v�f���]����łȂ�");
			}
		}
// 6/25�R�����g��
//      	/**
//      	 * �]������X�g�̃\�[�g���`�F�b�N
//      	 */
//		if (hyokaKijunList.size() > 1) {
//			for (int j = 0; j < hyokaKijunList.size() - 1; j++) {
//			    // ���X�g�f�[�^���Q���ȏ㑶�݂���ꍇ
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
//							"�]������X�g���]���R�[�h�̏����łȂ�");
//			    }    
//			}
//		}
    }

    /**
     * 
     * ��񐶐�
     * 
     * @param kanriNo ��������
     * @return hyokaKijunList<HyokaKijun> �]������X�g
     * 
     */
    private List listHyokaKijunMaker(String kanriNo)
    	throws NoSuchDataException {
        // DAO�̎擾
        final UPDataAccessObject dao = getUPDataAccessObject();
//        UtilLog.debug(this.getClass(),
//        			  "\n--- sqlHelper = [" + this.jg00006SQL + "]");
        // SQL��ݒ�
        dao.setSQL(new SQLHelper("JG00006"));

        // �o�C���h�l�̐ݒ�
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
        	//2007-01-17 �N���[�Y�R��Ή�
        	dao.clearSQL();
        }
    }

    /**
     * 
     * ���O�����`�F�b�N
     * 
     * @param kanriNo ��������
     */
    private void selfTestListHyokaKijunPre(String kanriNo) {

        if (kanriNo == null) {
            throw new 
			BusinessRuleException("�Ǘ��ԍ��� null �͐ݒ�ł��܂���B");
        }
    }

    /**
     * �w���̕]�����̒l���擾����(�ǍĎ�)
     * 
     * @param condition ��������
     * @return hyokaCd �]���R�[�h
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    public String acquireHyokaMeiTuisaishi(HyokaCondition condition)
    	throws NoSuchDataException {
        // ���O�����`�F�b�N
        selfTestAcquireHyokaMeiTuisaishiPre(condition);
        // ��񐶐�
        String hyokaCd = makeacquireHyokaMeiTuisaishi(condition);
        // ����`�F�b�N
        selfTestAcquireHyokaMeiTuisaishiPost(hyokaCd);
        
        return hyokaCd;
    }

    /**
     * 
     * ��񐶐�
     * 
     * @param condition ��������
     * @return hyokaCd �]���R�[�h
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    private String acquireHyokaMeiTuisaishiMaker(HyokaCondition condition)
    	throws NoSuchDataException {
    	return new SaitenServiceMockDao().acquireHyokaMeiTuisaishiOkNg(condition);
    }

    /**
     * 
     * ��񐶐�
     * 
     * @param condition ��������
     * @return hyokaCd �]���R�[�h
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    private String makeacquireHyokaMeiTuisaishi(HyokaCondition condition)
    	throws NoSuchDataException {
		// �ǍĎ����Ώێ�DAO����
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
	    		throw new NoSuchDataException("�w�肵���f�[�^��������܂���B");
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
     * ���O�����`�F�b�N
     * 
     * @param condition ��������
     * 
     */
    private void selfTestAcquireHyokaMeiTuisaishiPre(HyokaCondition condition) {

        if (condition == null) {
            throw new BusinessRuleException("���������� null �͐ݒ�ł��܂���B");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("�J�u�N�x�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("�w��No�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getJugyoCd() == null) {
            throw new BusinessRuleException("���ƃR�[�h�� null �͐ݒ�ł��܂���B");
        }
	
        if (condition.getSikenKaisu() == null) {
            throw new BusinessRuleException("�����񐔂� null �͐ݒ�ł��܂���B");
        }

        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException("�Ǘ��ԍ��� null �͐ݒ�ł��܂���B");
        }

        if (!"1".equals(condition.getShikenKbn()) && !"2".equals(condition.getShikenKbn())) {
            throw new BusinessRuleException("�����敪�� 1,2�ȊO�͐ݒ�ł��܂���B");
        }
    }
    
    /**
     * 
     * ��������`�F�b�N
     * 
     * @param condition ��������
     * 
     */
    private void selfTestAcquireHyokaMeiTuisaishiPost(String hyokaCd) {

    	if(hyokaCd == null){
    		throw new BusinessRuleException("�]���R�[�h�� null �͐ݒ�ł��܂���B");
    	}
    }

    /**
     * 
     * �w���̕]�����̒l���擾����(��������̂�)
     * 
     * @param condition ��������
     * @return hyokaCd �]���R�[�h
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    public String acquireHyokaMeiTeiki(HyokaCondition condition)
    	throws NoSuchDataException {
        // ���O�����`�F�b�N
        selfTestAcquireHyokaMeiTeikiPre(condition);
        // ��񐶐�
        //String hyokaCd = acquireHyokaMeiTeikiMaker(condition);
        String hyokaCd = makeacquireHyokaMeiTeiki(condition);
        // ��������`�F�b�N
        selfTestAcquireHyokaMeiTeikiPost(hyokaCd);
        
        return hyokaCd;
    }

    /**
     * 
     * ��񐶐�
     * 
     * @param condition ��������
     * @return hyokaCd �]���R�[�h
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    private String acquireHyokaMeiTeikiMaker(HyokaCondition condition)
    	throws NoSuchDataException {
    	return new SaitenServiceMockDao().acquireHyokaMeiTeikiOkNg(condition);
    }

    /**
     * 
     * ��񐶐�
     * 
     * @param condition ��������
     * @return hyokaCd �]���R�[�h
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    private String makeacquireHyokaMeiTeiki(HyokaCondition condition)
    	throws NoSuchDataException {
		// ���C�̓_DAO����
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
	    		throw new NoSuchDataException("�w�肵���f�[�^��������܂���B");
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
     * ���O�����`�F�b�N
     * 
     * @param condition ��������
     * 
     */
    private void selfTestAcquireHyokaMeiTeikiPre(HyokaCondition condition) {
    	
        if (condition == null) {
            throw new BusinessRuleException("���������� null �͐ݒ�ł��܂���B");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("�J�u�N�x�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("�w��No�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getJugyoCd() == null) {
            throw new BusinessRuleException("���ƃR�[�h�� null �͐ݒ�ł��܂���B");
        }

        // TODO ��������ɂ͎����񐔕s�v
//        if (condition.getSikenKaisu() == null) {
//            throw new BusinessRuleException("�����񐔂� null �͐ݒ�ł��܂���B");
//        }

        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException("�Ǘ��ԍ��� null �͐ݒ�ł��܂���B");
        }

        if (!"0".equals(condition.getShikenKbn())) {
            throw new BusinessRuleException("�����敪�� 0�ȊO�͐ݒ�ł��܂���B");
        }
    }
    
    /**
     * 
     * ��������`�F�b�N
     * 
     * @param condition ��������
     * 
     */
    private void selfTestAcquireHyokaMeiTeikiPost(String hyokaCd) {

    	if(hyokaCd == null){
    		throw new BusinessRuleException("�]���R�[�h�� null �͐ݒ�ł��܂���B");
    	}
    }

    /**
     * 
     * ���Ƃ��Ƃ̊w���̑f�_���擾����(�ǍĎ�)
     * 
     * @param condition ��������
     * @return hyokaKijunList<HyokaKijun> �]���
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    public String acquireHyokaSotenTsuisaishi(HyokaCondition condition)
    	throws NoSuchDataException {
        // ���O�����`�F�b�N
        selfTestAcquireHyokaSotenTsuisaishiPre(condition);
        // ��񐶐�
        String soten = makeacquireHyokaSotenTuisaishi(condition);
        // ����`�F�b�N
        selfTestAcquireHyokaSotenTuisaishiPost(soten);

        return soten;
    }

    /**
     * 
     * ��񐶐�
     * 
     * @param condition ��������
     * @return soten �f�_
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    private String acquireHyokaSotenTsuisaishiMaker(HyokaCondition condition)
    	throws NoSuchDataException {
    	return new SaitenServiceMockDao().acquireHyokaSotenTsuisaishiOkNg(condition);
    }

    /**
     * 
     * ��񐶐�
     * 
     * @param condition ��������
     * @return hyokaCd �]���R�[�h
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    private String makeacquireHyokaSotenTuisaishi(HyokaCondition condition)
    	throws NoSuchDataException {
		// �ǍĎ����Ώێ�DAO����
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
	    		throw new NoSuchDataException("�w�肵���f�[�^��������܂���B");
	    	}
			
	    	// 6/25 �C��St����������������������
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
	    	// 6/25 �C��En����������������������
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
     * ���O�����`�F�b�N
     * 
     * @param condition ��������
     * 
     */
    private void selfTestAcquireHyokaSotenTsuisaishiPre(HyokaCondition condition) {
    	
        if (condition == null) {
            throw new BusinessRuleException("���������� null �͐ݒ�ł��܂���B");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("�J�u�N�x�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("�w��No�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getJugyoCd() == null) {
            throw new BusinessRuleException("���ƃR�[�h�� null �͐ݒ�ł��܂���B");
        }
	
        if (condition.getSikenKaisu() == null) {
            throw new BusinessRuleException("�����񐔂� null �͐ݒ�ł��܂���B");
        }

        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException("�Ǘ��ԍ��� null �͐ݒ�ł��܂���B");
        }

        if (!("1".equals(condition.getShikenKbn()) 
        		|| "2".equals(condition.getShikenKbn()))) {
            throw new BusinessRuleException("�����敪�� 1,2�ȊO�͐ݒ�ł��܂���B");
        }
    }
    
    /**
     * 
     * ����`�F�b�N
     * 
     * @param condition ��������
     * 
     */
    private void selfTestAcquireHyokaSotenTuisaishiPost(String soten) {

    	if(soten == null){
    		throw new BusinessRuleException("�f�_�� null �͐ݒ�ł��܂���B");
    	}
    }

    /**
     * 
     * ���Ƃ��Ƃ̊w���̑f�_���擾����(��������̂�)
     * 
     * @param condition ��������
     * @return soten �f�_
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    public String acquireHyokaSotenTeiki(HyokaCondition condition)
    	throws NoSuchDataException {
        // ���O�����`�F�b�N
        selfTestAcquireHyokaSotenTeikiPre(condition);
        // ��񐶐�
        String soten = makeacquireHyokaSotenTeiki(condition);
        // ��������`�F�b�N
        selfTestAcquireHyokaSotenTeikiPost(soten);

        return soten;
    }

    /**
     * 
     * ��񐶐�
     * 
     * @param condition ��������
     * @return soten �f�_
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    private String acquireHyokaSotenTeikiMaker(HyokaCondition condition)
    	throws NoSuchDataException {
    	return new SaitenServiceMockDao().acquireHyokaSotenTeikiOkNg(condition);
    }

    /**
     * 
     * ��񐶐�
     * 
     * @param condition ��������
     * @return soten �f�_
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    private String makeacquireHyokaSotenTeiki(HyokaCondition condition)
    	throws NoSuchDataException {
		// ���C�̓_DAO����
    	KmgRisySitnUPDAO rsyStndAO = (KmgRisySitnUPDAO) getDbSession().getDao(KmgRisySitnUPDAO.class);
    	try{
    		
    		int shikenKaisu = 0;
    		if ("0".equals(condition.getShikenKbn())) {
    			// ��������̏ꍇ�͎����񐔂��P�Œ�
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
	    		throw new NoSuchDataException("�w�肵���f�[�^��������܂���B");
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
     * ���O�����`�F�b�N
     * 
     * @param condition ��������
     * 
     */
    private void selfTestAcquireHyokaSotenTeikiPre(HyokaCondition condition) {
    	
        if (condition == null) {
            throw new BusinessRuleException("���������� null �͐ݒ�ł��܂���B");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("�J�u�N�x�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("�w��No�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getJugyoCd() == null) {
            throw new BusinessRuleException("���ƃR�[�h�� null �͐ݒ�ł��܂���B");
        }
	// TODO ��������ɂ͎����񐔕s�v
//        if (condition.getSikenKaisu() == null) {
//            throw new BusinessRuleException("�����񐔂� null �͐ݒ�ł��܂���B");
//        }

        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException("�Ǘ��ԍ��� null �͐ݒ�ł��܂���B");
        }

        if (!"0".equals(condition.getShikenKbn())) {
            throw new BusinessRuleException("�����敪�� 0�ȊO�͐ݒ�ł��܂���B");
        }
    }
    
    /**
     * 
     * ����`�F�b�N
     * 
     * @param condition ��������
     * 
     */
    private void selfTestAcquireHyokaSotenTeikiPost(String soten) {

    	if(soten == null){
    		throw new BusinessRuleException("�f�_�� null �͐ݒ�ł��܂���B");
    	}
    }

	/**
	 * 
	 * �w���̑f�_�܂��͕]�����̒l���X�V����B(�ǍĎ��̏ꍇ)
	 * 
	 * @param hyokaKoshinValue �ݒ���
	 * @throws AlreadyUpdatePossibilityException
	 * @throws AlreadyUpdateException
	 * @throws NoSuchDataException
	 */
	 public void updateTuisaishi(HyokaKoshinValue value) 
	 	throws AlreadyUpdatePossibilityException, 
		AlreadyUpdateException, 
		NoSuchDataException {
	    
	    // ���O�����`�F�b�N
	    selfTestUpdateTuisaishiPre(value);
	    try {
			// �ǍĎ����Ώێ�TBL�X�V
			updateKmgTisiGak(value);
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		} catch (GakuenException e) {
			throw new GakuenSystemException(e);
		}
	}

	/**
	 * 
	 * �ǍĎ����Ώێ�TBL���X�V����
	 * 
	 * @param hyokaKoshinValue �ݒ���
	 * @throws AlreadyUpdatePossibilityException 
	 * ���ɍX�V����Ă���\��������ꍇ�̗�O
	 * @throws AlreadyUpdateException ���Ƀf�[�^���X�V����Ă���ꍇ�̗�O
	 * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
	 * @throws DbException
	 * @throws GakuenException
	 */
    private void updateKmgTisiGak(HyokaKoshinValue value) 
    	throws AlreadyUpdatePossibilityException,
		AlreadyUpdateException,
		NoSuchDataException, DbException, GakuenException {

		// �ǍĎ����Ώێ�DAO����
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
	    		throw new NoSuchDataException("�w�肳�ꂽ�f�[�^��������܂���B");
	    	}

	    	String unyoHoho = value.getSaitenKbn(); // �f�_
	    	
	    	// �f�_�^�p�̏ꍇ
	    	if (unyoHoho.equals("1")) {
	    		// �]���_��ݒ�
	    		ar.setHyokaTen(value.getHyokaTen());
	    		
	    		if (ar.getHyokaCd() != null
	    				&& !"".equals(ar.getHyokaCd())) {
	    			// �]���R�[�h���ݒ肳��Ă���ꍇ�A
	    			// ���A�f�_���ݒ肳��Ă���ꍇ�́A�]���R�[�h���N���A����B
	    			if (value.getHyokaTen() != null) {
	    	    		ar.setHyokaCd(null);
	    			}
	    		}
	    	}
	    	
	    	// �]�����̉^�p�̏ꍇ
	    	if (unyoHoho.equals("2")) {
	    		// �]���R�[�h��ݒ�
	    		ar.setHyokaCd(value.getHyokaCd());
	    		
	    		if (ar.getHyokaTen() != null) {
	    			// �f�_���ݒ肳��Ă���ꍇ�A
	    			// ���A�]���R�[�h���ݒ肳��Ă���ꍇ�́A�f�_���N���A����B
	    			if (value.getHyokaCd() != null
		    				&& !"".equals(value.getHyokaCd())) {
	    	    		ar.setHyokaTen(null);
	    			}
	    		}
	    	}
	    	
//	    	// �̓_�X�V��
//	    	ar.setSitnJinjiCd(value.getSitnJinjiCd());
//	    	
//	    	// �̓_�X�V��
//	    	ar.setSitnUpdateDate(
//	    			UtilDate.cnvSqlDate(value.getSitnUpdateDate()));

			// �ǍĎ����Ώێ�TBL���X�V
	    	ar.store();
	    	
    	}catch (DbException e){
    		throw new GakuenSystemException(e);
    	}
    }

    /**
     * 
     *  ���O�����`�F�b�N
     * 
     * @param hyokaKoshinValue �ݒ���
     */
    private void selfTestUpdateTuisaishiPre(HyokaKoshinValue value) {
    	
        if (value == null) {
            throw new BusinessRuleException("�o�^������ null �͐ݒ�ł��܂���B");
        }
        
        if (value.getKaikoNendo() == null) {
            throw new BusinessRuleException("�J�u�N�x�� null �͐ݒ�ł��܂���B");
        }

        if (value.getGakkiNo() == null) {
            throw new BusinessRuleException("�w��No�� null �͐ݒ�ł��܂���B");
        }

        if (value.getJugyoCd() == null) {
            throw new BusinessRuleException("���ƃR�[�h�� null �͐ݒ�ł��܂���B");
        }

        if (value.getTuisaisikenKbn() == null) {
            throw new BusinessRuleException("�ǍĎ����敪�� null �͐ݒ�ł��܂���B");
        }
        
        if (!"1".equals(value.getTuisaisikenKbn()) && !"2".equals(value.getTuisaisikenKbn())) {
            throw new BusinessRuleException("�ǍĎ����敪�� 1,2�ȊO�͐ݒ�ł��܂���B");
        }
        
    	if (value.getSikenKaisu() == null) {
    		throw new BusinessRuleException("�����񐔂� null �͐ݒ�ł��܂���B");
    	}

        if (value.getKanriNo() == null) {
            throw new BusinessRuleException("�Ǘ��ԍ��� null �͐ݒ�ł��܂���B");
        }
        
        if (value.getSitnJinjiCd() == null) {
            throw new BusinessRuleException("�̓_�X�V�҂� null �͐ݒ�ł��܂���B");
        }
        
        if (value.getSitnUpdateDate() == null) {
            throw new BusinessRuleException("�̓_�X�V���� null �͐ݒ�ł��܂���B");
        }
        
//    	String unyoHoho = value.getSaitenKbn();
//    	
//    	// �f�_�^�p�̏ꍇ
//    	if (unyoHoho.equals("1")) {
//    		if (value.getHyokaTen() == null) {
//    			throw new BusinessRuleException("�]���_�� null �͐ݒ�ł��܂���B");
//    		}
//    	}
//    	
//    	// �]�����̉^�p�̏ꍇ
//    	if (unyoHoho.equals("2")) {
//    		if (value.getHyokaCd() == null) {
//    			throw new BusinessRuleException("�]���R�[�h�� null �͐ݒ�ł��܂���B");
//    		}
//    	}	
    }
    
	/**
	 * 
	 * �w���̑f�_�܂��͕]�����̒l���X�V����B(��������̏ꍇ)
	 * 
	 * @param hyokaKoshinValue �ݒ���
	 * @throws NoSuchDataException
	 * @throws AlreadyUpdatePossibilityException
	 * @throws AlreadyUpdateException
	 * 
	*/
	public void updateTeiki(HyokaKoshinValue value) 
		throws NoSuchDataException, 
		AlreadyUpdatePossibilityException, 
		AlreadyUpdateException {
	    
	    // ���O�����`�F�b�N
	    selfTestUpdateTeikiPre(value);
	    try {
			// ���X�V
			updateKmgRisySitn(value);
		} catch (GakuenException e) {
			throw new GakuenSystemException(e);
		} catch (DbException e) {
			throw new GakuenSystemException(e);
		}
	}

	/**
	 * 
	 * ���C�̓_TBL���X�V����
	 * 
	 * @param value �ݒ���
	 * @throws AlreadyUpdatePossibilityException 
	 * ���ɍX�V����Ă���\��������ꍇ�̗�O
	 * @throws AlreadyUpdateException ���Ƀf�[�^���X�V����Ă���ꍇ�̗�O
	 * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
	 * @throws GakuenException
	 * @throws DbException
	 * 
	 */
    private void updateKmgRisySitn(HyokaKoshinValue value) 
    	throws NoSuchDataException,
        AlreadyUpdatePossibilityException,
		AlreadyUpdateException, GakuenException, DbException {
		// ���C�̓_DAO����
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
	    		throw new NoSuchDataException("�w�肳�ꂽ�f�[�^��������܂���B");
	    	}

	    	final String unyoHoho = value.getSaitenKbn();
	    	// �f�_�^�p�̏ꍇ
	    	if (unyoHoho.equals(String.valueOf(SaitenKbn.SOTEN.getCode()))) {
	    		// �]���_��ݒ�
	    		ar.setHyokaTen(value.getHyokaTen());
	    		if (ar.getHyokaCd() != null
	    				&& !"".equals(ar.getHyokaCd())) {
	    			// �]���R�[�h���ݒ肳��Ă���ꍇ�A
	    			// ���A�f�_���ݒ肳��Ă���ꍇ�́A�]���R�[�h���N���A����B
	    			if (value.getHyokaTen() != null) {
	    	    		ar.setHyokaCd(null);
	    			}
	    		}
	    	}
	    	// �]�����̉^�p�̏ꍇ
	    	if (unyoHoho.equals(String.valueOf(
	    			SaitenKbn.HYOKANAME.getCode()))) {
	    		// �]���R�[�h��ݒ�
	    		ar.setHyokaCd(value.getHyokaCd());
	    		if (ar.getHyokaTen() != null) {
	    			// �f�_���ݒ肳��Ă���ꍇ�A
	    			// ���A�]���R�[�h���ݒ肳��Ă���ꍇ�́A�f�_���N���A����B
	    			if (value.getHyokaCd() != null
		    				&& !"".equals(value.getHyokaCd())) {
	    	    		ar.setHyokaTen(null);
	    			}
	    		}
	    	}
	    	
//	    	// �̓_�X�V��
//	    	ar.setSitnJinjiCd(value.getSitnJinjiCd());
//	    	
//	    	// �̓_�X�V��
//	    	ar.setSitnUpdateDate(
//	    			UtilDate.cnvSqlDate(value.getSitnUpdateDate()));

			// �ǍĎ����Ώێ�TBL���X�V
	    	ar.store();
	    	
    	}catch (DbException e){
    		throw new GakuenSystemException(e);
    	}
    }

    /**
     * 
     *  ���O�����`�F�b�N
     * 
     * @param hyokaKoshinValue �ݒ���
     */
    private void selfTestUpdateTeikiPre(HyokaKoshinValue value) {

    	if (value == null) {
    		throw new BusinessRuleException("�o�^������ null �͐ݒ�ł��܂���B");
    	}
       
    	if (value.getKaikoNendo() == null) {
    		throw new BusinessRuleException("�J�u�N�x�� null �͐ݒ�ł��܂���B");
    	}

    	if (value.getGakkiNo() == null) {
    		throw new BusinessRuleException("�w��No�� null �͐ݒ�ł��܂���B");
    	}

    	if (value.getJugyoCd() == null) {
    		throw new BusinessRuleException("���ƃR�[�h�� null �͐ݒ�ł��܂���B");
    	}
    	
//    	if (value.getSikenKaisu() == null) {
//    		throw new BusinessRuleException("�����񐔂� null �͐ݒ�ł��܂���B");
//    	}

    	if (value.getKanriNo() == null) {
    		throw new BusinessRuleException("�Ǘ��ԍ��� null �͐ݒ�ł��܂���B");
    	}
       
    	if (value.getSitnJinjiCd() == null) {
    		throw new BusinessRuleException("�̓_�X�V�҂� null �͐ݒ�ł��܂���B");
    	}
       
    	if (value.getSitnUpdateDate() == null) {
    		throw new BusinessRuleException("�̓_�X�V���� null �͐ݒ�ł��܂���B");
    	}
    	
//    	String unyoHoho = value.getSaitenKbn();
//    	
//    	// �f�_�^�p�̏ꍇ
//    	if (unyoHoho.equals("1")) {
//    		if (value.getHyokaTen() == null) {
//    			throw new BusinessRuleException("�]���_�� null �͐ݒ�ł��܂���B");
//    		}
//    	}
//    	
//    	// �]�����̉^�p�̏ꍇ
//    	if (unyoHoho.equals("2")) {
//    		if (value.getHyokaCd() == null) {
//    			throw new BusinessRuleException("�]���R�[�h�� null �͐ݒ�ł��܂���B");
//    		}
//    	}	    	
    }
    

    /**
     * �w�肵���̓_(�ǍĎ���)��CSV�o�͍ςɂ���
     * @param dto
     */
    public void updateTsusaishikenCsvOut(SaitenStatusDTO dto) {
    	
        //���O�����`�F�b�N
        selfTestTsusaishikenCsvOutPre(dto);

        // ��񐶐�
        updateTsusaishiken(dto);
    }

    /**
     * �f�[�^�X�V
     * �w�肵���̓_(�ǍĎ���)��CSV�o�͍ςɂ���
     * @param dto
     */
    private void updateTsusaishiken(SaitenStatusDTO dto) {
    	
		// �̓_�o�^�󋵁Q���������DAO����
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
    			
    			// TODO �萔�N���X�𗘗p����ׂ�
    			// �����Ώۂ��������ꍇ�͍X�V
    			stnTuisaiAR.setCsvOutputFlg(1);
    		} else {
    			// �����Ώۂ��Ȃ��ꍇ�͐V�K�ǉ�
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
    		
    		// �̓_�X�V��
    		stnTuisaiAR.setSitnJinjiCd(getDbSession().getUserId());
    		
    		// �̓_�X�V��
    		stnTuisaiAR.setSitnUpdateDate(getDbSession().getCurrentTime());
    		
    		
    		stnTuisaiAR.store();
// ���� UPEX-1199�Ή� 2010.1.28 h.matsuda add start
    		// �����������}����ׁA�L���b�V�������Ȃ�
    		stnTuisaiDAO.destroy();    		
// ���� UPEX-1199�Ή� 2010.1.28 h.matsuda add end 
    		
    	} catch (DbException dbe) {
   			throw new GakuenSystemException(
   					"updateTsusaishiken KmcStnTuisaiAR", dbe);
   			
   		} catch (GakuenException ge) {
   			throw new GakuenSystemException(
   					"updateTsusaishiken KmcStnTuisaiAR", ge);
   		}
        
    }

    /**
     * ���O����
     * �u�w�肵���̓_(�ǍĎ���)��CSV�o�͍ςɂ���v�̌����������`�F�b�N����
     * @param dto
     */
    private void selfTestTsusaishikenCsvOutPre(SaitenStatusDTO dto) {

        if (dto == null) {
            throw
			new BusinessRuleException("�̓_�󋵂� null �͐ݒ�ł��܂���B");
        }
        if (dto.getNendo() == null) {
            throw
			new BusinessRuleException("�N�x�� null �͐ݒ�ł��܂���B");
        }
        if (dto.getGakkiNo() == null) {
            throw
			new BusinessRuleException("�w���m���� null �͐ݒ�ł��܂���B");
        }
        if (dto.getJugyoCode() == null) {
            throw
			new BusinessRuleException("���ƃR�[�h�� null �͐ݒ�ł��܂���B");
        }
        if (dto.getKanriNo() == null) {
            throw
			new BusinessRuleException("�Ǘ��m���� null �͐ݒ�ł��܂���B");
        }
        if (dto.getShikenKaisu() == null) {
            throw
			new BusinessRuleException("�����񐔂� null �͐ݒ�ł��܂���B");
        }
        if (dto.getShikenKbn() == null) {
            throw
			new BusinessRuleException("�����敪�� null �͐ݒ�ł��܂���B");
        }           
        

    }

    /**
     * �w�肵���̓_(�������)��CSV�o�͍ςɂ���
     * @param list
     */
    public void updateTeikiShikenCsvOut(SaitenStatusDTO dto) {

        //���O�����`�F�b�N
        selfTestTeikiShikenCsvOutPre(dto);

        // ��񐶐�
        updateTeikiShiken(dto);
    }

    /**
     * ���̍X�V
     * @param list
     */
    private void updateTeikiShiken(SaitenStatusDTO dto) {

    	   	
		// �̓_�o�^�󋵁Q���������DAO����
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
    			// �����f�[�^������ꍇ�͍X�V
    			// TODO �萔�N���X�ɒu������
    			stnTeikiAR.setCsvOutputFlg(1);				
    			
    		} else {
    			// �����Ώۂ��Ȃ��������ߐV�K�ǉ�
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
    			
    			//TODO �萔�N���X�ɒu������
    			stnTeikiAR.setCsvOutputFlg(1);    			
    		}
    		
    		// �̓_�X�V��
    		stnTeikiAR.setSitnJinjiCd(getDbSession().getUserId());
    		
    		// �̓_�X�V��
    		stnTeikiAR.setSitnUpdateDate(getDbSession().getCurrentTime());
    		
    		stnTeikiAR.store();
// ���� UPEX-1199�Ή� 2010.1.28 h.matsuda add start
    		// �����������}����ׁA�L���b�V�������Ȃ�
    		stnTeikiDAO.destroy();    		
// ���� UPEX-1199�Ή� 2010.1.28 h.matsuda add end    		
   		} catch (DbException dbe) {
   			throw new GakuenSystemException(
   					"updateTeikiShiken KmcStnTeikiAR", dbe);
   			
   		} catch (GakuenException ge) {
   			throw new GakuenSystemException(
   					"updateTeikiShiken KmcStnTeikiAR", ge);
   		}
	
    }

    /**
     * ���O�����@
     * �u�w�肵���̓_(�������)��CSV�o�͍ςɂ���v�̌����������`�F�b�N����@
     * @param list
     */
    private void selfTestTeikiShikenCsvOutPre(SaitenStatusDTO dto) {
        if (dto == null) {
            throw new BusinessRuleException(
            		"�̓_�󋵂� null �͐ݒ�ł��܂���B");
        }

        if (dto.getNendo() == null) {
            throw 
			new BusinessRuleException("�N�x�� null �͐ݒ�ł��܂���B");
        }
        if (dto.getGakkiNo() == null) {
            throw
			new BusinessRuleException("�w���m���� null �͐ݒ�ł��܂���B");
        }
        if (dto.getJugyoCode() == null) {
            throw
			new BusinessRuleException("���ƃR�[�h�� null �͐ݒ�ł��܂���B");
        }
        if (dto.getKanriNo() == null) {
            throw
			new BusinessRuleException("�Ǘ��m���� null �͐ݒ�ł��܂���B");
        }
//        if (dto.getShikenKaisu() == null) {
//            throw
//			new BusinessRuleException("�����񐔂� null �͐ݒ�ł��܂���B");
//        }
        if (dto.getShikenKbn() == null) {
            throw
			new BusinessRuleException("�����敪�� null �͐ݒ�ł��܂���B");
        }
    }

    /**
     * @param condition
     * @param saitenUnyo
     * @return
     * @throws NoSuchDataException
     */
    public SaitenUnyoDTO acquireSaitenUnyo(SaitenUnyoTblCondition condition) throws NoSuchDataException {
        //���O�����`�F�b�N
        selfTestAcquireSaitenUnyoPre(condition);

        // ��񐶐�
        SaitenUnyoDTO saitenUnyo = makeSaitenUnyo(condition);

        //��������`�F�b�N
        selfTestAcquireSaitenUnyoPost(saitenUnyo);

        return saitenUnyo;
    }

    /**
     * @param saitenUnyo
     */
    private void selfTestAcquireSaitenUnyoPost(SaitenUnyoDTO saitenUnyo) {
        if (saitenUnyo == null) {
            throw new BusinessRuleException("�̓_�^�p�� null �͐ݒ�ł��܂���B");
        }
    	if (saitenUnyo.getGakkiNo() == null) {
    		throw new BusinessRuleException("�̓_�^�p�̊w���m�n�� null �͐ݒ�ł��܂���B");
    	}
    	if (saitenUnyo.getKaikoNendo() == null) {				 
    		throw new BusinessRuleException("�̓_�^�p�̊J�u�N�x�� null �͐ݒ�ł��܂���B");
    	}
    	if (saitenUnyo.getKanriBsyoCd() == null) {				 
    		throw new BusinessRuleException("�̓_�^�p�̊Ǘ������R�[�h�� null �͐ݒ�ł��܂���B");
    	}			   											 
    	if (saitenUnyo.getSaitenKaishibi() == null) {			 
    		throw new BusinessRuleException("�̓_�^�p�̍̓_�J�n������ null �͐ݒ�ł��܂���B");
    	}			   											 
    	if (saitenUnyo.getSaitenKbn() == null) {				 
    		throw new BusinessRuleException("�̓_�^�p�̍̓_���@�敪�� null �͐ݒ�ł��܂���B");
    	}			   											 
    	if (saitenUnyo.getSaitenShuryobi() == null) {			 
    		throw new BusinessRuleException("�̓_�^�p�̍̓_�I�������� null �͐ݒ�ł��܂���B");
    	}			   											 
    	if (saitenUnyo.getShikenKbn() == null) {				 
    		throw new BusinessRuleException("�̓_�^�p�̎����敪�� null �͐ݒ�ł��܂���B");
    	}
    }

    /**
     * @param condition
     * @throws NoSuchDataException
     */
    private SaitenUnyoDTO makeSaitenUnyo(SaitenUnyoTblCondition condition) throws NoSuchDataException {
        
      // SQL��ݒ�
      final SQLHelper sqlHelper = new SQLHelper(ISQLContents.ID_KM + "00043");

      // SQL��ݒ� �̓_�󋵂̈ꗗ(�������)���擾
      getUPDataAccessObject().setSQL(sqlHelper);
      // �f�[�^�ϊ��I�u�W�F�N�g�𐶐�
      final SaitenUnyoExchanger exchanger = new SaitenUnyoExchanger();

      try {
	      return exchanger.acquireSaitenUnyo(getUPDataAccessObject().find(
	              exchanger.preparedStatementBindingValues(condition)));
      } finally {
    	//2007-01-17 �N���[�Y�R��Ή�
      	getUPDataAccessObject().clearSQL();
      }

    }

    /**
     * @param condition
     */
    private void selfTestAcquireSaitenUnyoPre(SaitenUnyoTblCondition condition) {
        if (condition == null) {
            throw new BusinessRuleException("���������� null �͐ݒ�ł��܂���B");
        }
        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("�J�u�N�x�� null �͐ݒ�ł��܂���B");
        }
        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("�w���m���� null �͐ݒ�ł��܂���B");
        }
        if (condition.getJugyoCd() == null) {
            throw new BusinessRuleException("���ƃR�[�h�� null �͐ݒ�ł��܂���B");
        }
        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException("�����敪�� null �͐ݒ�ł��܂���B");
        }
    }

    /**
     *  ���O�����`�F�b�N
     * <BR>
     * @param condition 
     * 
     */
    private void selfTestListSaitenUnyouPre(SaitenCondition condition) {
    	
    	if (condition == null) {
    		throw new BusinessRuleException("condition��null �G���[");
    	}
    	
    	if (condition.getNendo() == null) {
    		throw new BusinessRuleException("nendo��null �G���[");
    	}
    	
    	if (condition.getGakkiNo() == null) {
    		throw new BusinessRuleException("gakkiNo��null �G���[");
    	}
    }
    
    /**
     * �̓_�^�p���@�̈ꗗ���擾����
     * <BR>
     * @param condition
     * @throws NoSuchDataException
     *  �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * @return List 
     * 
     */
    public List listSaitenUnyou(SaitenCondition condition) 
    	throws NoSuchDataException {
 
    	// ���O����
    	selfTestListSaitenUnyouPre(condition);
    	// ��񐶐�
    	List list = makeListSaitenUnyou(condition);
    	// �������
    	selfTestListSaitenUnyouPost(list);
    	
        return list;
    }
    
	/**
	 * 
	 * ��񐶐�
	 * 
	 * @param SaitenCondition 
	 * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
	 */
    private List makeListSaitenUnyou(SaitenCondition condition) 
    	throws NoSuchDataException {
        // DAO�̎擾
        final UPDataAccessObject dao = getUPDataAccessObject();

        // SQL��ݒ�
        final SQLHelper sqlHelper = new SQLHelper(ISQLContents.ID_KM + "00108");

        
        UtilLog.debug(this.getClass(),
        			  "\n--- sqlHelper = [" + sqlHelper + "]");
        // SQL��ݒ�
        dao.setSQL(sqlHelper);


        // �f�[�^�ϊ��I�u�W�F�N�g�𐶐�
        final SaitenUnyoExchanger exchanger = new SaitenUnyoExchanger();

        try {
	        return exchanger.listSaitenUnyou(getUPDataAccessObject().find(
	                exchanger.preparedStatementBindingValues(condition)));
        } finally {
        	//2007-01-17 �N���[�Y�R��Ή�
        	dao.clearSQL();
        }
    }
    
    /**
     * ���̓_�����̈ꗗ���擾����
     * <BR>
     * @param condition
     * @throws NoSuchDataException
     *  �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * @return List 
     * 
     */
    public List acquireMiSaitenKyoinList(MisaitenJugyoCondition condition)
    	throws NoSuchDataException {
 
    	// ���O����
    	selfTestListMiSaitenKyoinPre(condition);
    	// ��񐶐�
    	List list = makeListMiSaitenKyoin(condition);
    	// �������
    	selfTestListMiSaitenKyoinPost(list);
    	
        return list;
    }
    
    /**
     *  ���O�����`�F�b�N
     * <BR>
     * @param condition 
     * 
     */
    private void selfTestListMiSaitenKyoinPre(MisaitenJugyoCondition condition) {
    	
    	if (condition == null) {
    		throw new BusinessRuleException("����������null �G���[");
    	}
    	
    	if (condition.getNendo() == null) {
    		throw new BusinessRuleException("�N�x��null �G���[");
    	}
    	
    	if (condition.getGakkiNo() == null) {
    		throw new BusinessRuleException("�w���m����null �G���[");
    	}

    	if (condition.getKanriBusyoCode() == null) {
    		throw new BusinessRuleException("�Ǘ������R�[�h��null �G���[");
    	}
    }
    
    
	/**
	 * 
	 * ��񐶐�
	 * 
	 * @param SaitenCondition 
	 * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
	 */
    private List makeListMiSaitenKyoin(MisaitenJugyoCondition condition) 
    	throws NoSuchDataException {
	    // DAO�̎擾
	    final UPDataAccessObject dao = getUPDataAccessObject();
	
	    // SQL���擾
	    SQLHelper sqlHelperTeiki = null;
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu Start
//	    SQLHelper sqlHelperTsuiSai = null;
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu End
	    SQLHelper sqlHelperKyoinSu = null;
	    SQLHelper sqlHelperJugyoSu = null;
	
	    // �f�[�^�ϊ��I�u�W�F�N�g(VE)�𐶐�
	    final SaitenValueExchanger ve
	        = new SaitenValueExchanger();
	    
		// ��������
		List listCondition = new ArrayList();
		List kyoinSuListCondition = new ArrayList();
		List listTeiki;
	    List listTsuiSai;
	    List listJugyo;

//		 UPEX-1385 2010/06/11 k.higashida Start
	    // �p�����[�^�e�[�u����藯�w�E�x�w�����O���邩�ǂ����̏����擾����
		String para = "";
		String kyugakKbn = "0";
		String ryugakKbn = "0";
        CouParamDAO couParaDao = (CouParamDAO) super.getDbSession().getDao(
                CouParamDAO.class);
        CouParamAR couParamAR;
        try {
        	couParamAR = couParaDao.findByPrimaryKey("KMC", "KYUGAK_RYUGAK_JOGAI", 0);
       		para = UtilStr.cnvNull(couParamAR.getValue());
       		
       		// �擾�����p�����[�^�[�̋敪�ɂ���ċx�w�E���w�̌���������ݒ肷��
       		if (para.equals("0")) {
       			// �x�w�A���w�����O���Ȃ�
       			kyugakKbn = "0";
       			ryugakKbn = "0";
       		} else if (para.equals("1")) {
       			// �x�w�̂ݏ��O����
       			kyugakKbn = "1";
       			ryugakKbn = "0";
       		} else if (para.equals("2")) {
       			// ���w�̂ݏ��O����
       			kyugakKbn = "0";
       			ryugakKbn = "1";
       		} else if (para.equals("3")) {
       			// �x�w�A���w�����O����
       			kyugakKbn = "1";
       			ryugakKbn = "1";
       		} else {
       			// �x�w�A���w�����O����(0,1,2,3�ȊO�̏ꍇ)
       			kyugakKbn = "1";
       			ryugakKbn = "1";
       		}
        } catch (DbException e) {
            throw new GakuenSystemException(e);
        } catch (NullPointerException e) {
            throw new GakuenSystemException("����:KMC�A����:KYUGAK_RYUGAK_JOGAI�̃p�����[�^�����݂��܂���",
                    e);
        } catch (Exception e) {
            throw new GakuenSystemException(e);
        }
//		 UPEX-1385 2010/06/11 k.higashida End
	    
	    // ���������ɂ��SQL���Ăѕ�����
	    if ("".equals(condition.getKanriBusyoCode())) {
	    	// �S�đΏۂ̏ꍇ
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu Start
//	    	sqlHelperTeiki =  new SQLHelper("KM00050");
//	    	sqlHelperTsuiSai =  new SQLHelper("KM00051");
	        sqlHelperTeiki =  new SQLHelper("KM00169");
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu End
	    	sqlHelperKyoinSu =  new SQLHelper("KM00056");
	    	sqlHelperJugyoSu =  new SQLHelper("KM00140");

//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu Start
//			listTeiki = ve.preparedTeikiBindingValues(condition);
//	    	 UPEX-1385 2010/06/11 k.higashida Start
//			listTeiki = ve.preparedTeikiTsuiSaiBindingValues(condition);
			listTeiki = ve.preparedTeikiTsuiSaiBindingValues(condition, kyugakKbn, ryugakKbn);
//	    	 UPEX-1385 2010/06/11 k.higashida End
//			listTsuiSai = ve.preparedTsuiSaiBindingValues(condition);
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu End
//	    	 UPEX-1385 2010/06/11 k.higashida Start
//			listJugyo = ve.preparedJugyoValues(condition);
			listJugyo = ve.preparedJugyoValues(condition, kyugakKbn, ryugakKbn);
//	    	 UPEX-1385 2010/06/11 k.higashida End
//UPEX-742 ���Ɛ�����̌����ɂ��킹�ċ��������̓_�^�p���Ԃ����� 2008.10.08 Horiguchi Start
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
//UPEX-742 ���Ɛ�����̌����ɂ��킹�ċ��������̓_�^�p���Ԃ����� 2008.10.08 Horiguchi End
	    } else {
	    	// �Ǘ������̎w�肠��̏ꍇ
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu Start
//	    	sqlHelperTeiki =  new SQLHelper("KM00052");
//	    	sqlHelperTsuiSai =  new SQLHelper("KM00053");
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu End
	        sqlHelperTeiki =  new SQLHelper("KM00170");
	    	sqlHelperKyoinSu =  new SQLHelper("KM00057");
	    	sqlHelperJugyoSu =  new SQLHelper("KM00141");
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu Start	    	
//			listTeiki = ve.preparedTeikiKanriBusyoBindingValues(condition);
//	    	 UPEX-1385 2010/06/11 k.higashida Start
//	    	listTeiki = ve.preparedTeikiTsuiSaiKanriBusyoBindingValues(condition);
	    	listTeiki = ve.preparedTeikiTsuiSaiKanriBusyoBindingValues(condition, kyugakKbn, ryugakKbn);
//	    	 UPEX-1385 2010/06/11 k.higashida End
//			listTsuiSai = ve.preparedTsuiSaiKanriBusyoBindingValues(condition);
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu End
//	    	 UPEX-1385 2010/06/11 k.higashida Start
//			listJugyo = ve.preparedJugyoForKanriBushoValues(condition);
			listJugyo = ve.preparedJugyoForKanriBushoValues(condition, kyugakKbn, ryugakKbn);
//	    	 UPEX-1385 2010/06/11 k.higashida End
//UPEX-742 ���Ɛ�����̌����ɂ��킹�ċ��������̓_�^�p���Ԃ����� 2008.10.08 Horiguchi Start
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
//UPEX-742 ���Ɛ�����̌����ɂ��킹�ċ��������̓_�^�p���Ԃ����� 2008.10.08 Horiguchi End
	    }

	    UtilLog.debug(this.getClass(), 
	    		"\n--- sqlHelperTeiki = [" + sqlHelperTeiki + "]");
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu Start
//	    UtilLog.debug(this.getClass(), 
//	    		"\n--- sqlHelperTsuiSai = [" + sqlHelperTsuiSai + "]");
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu End
	    UtilLog.debug(this.getClass(), 
	    		"\n--- sqlHelperKyoinSu = [" + sqlHelperKyoinSu + "]");
	    UtilLog.debug(this.getClass(), 
	    		"\n--- sqlHelperJugyoSu = [" + sqlHelperJugyoSu + "]");

		// SQL��ݒ�
		dao.setSQL(sqlHelperKyoinSu);

		// ������
		int kyoinSu = 0;
		try {
			kyoinSu =
			   	ve.getKyoinSu(getUPDataAccessObject().
			   					find(kyoinSuListCondition));
		} finally {
        	//2007-01-17 �N���[�Y�R��Ή�
        	dao.clearSQL();
        }
		
		// SQL��ݒ�
		dao.setSQL(sqlHelperJugyoSu);

		// ���Ɛ�
		int jugyoSu = 0;
		try {
			jugyoSu = 
				ve.getJugyoSu(getUPDataAccessObject().find(listJugyo));
		} finally {
        	//2007-01-17 �N���[�Y�R��Ή�
        	dao.clearSQL();
        }

		// SQL��ݒ�
		dao.setSQL(sqlHelperTeiki);

		// ��������̖��̓_�����ꗗ
		Map misaitenTeikiMap = null;

		try {		
			misaitenTeikiMap =
				ve.mapMiSaitenKyoin(getUPDataAccessObject().find(listTeiki),
						misaitenTeikiMap);
		} catch (NoSuchDataException e) {
			// �Y���f�[�^���Ȃ��ꍇ����B
			misaitenTeikiMap = new HashMap();
		} finally {
        	//2007-01-17 �N���[�Y�R��Ή�
        	dao.clearSQL();
        }
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu Start
//		dao.setSQL(sqlHelperTsuiSai);

		// �ǎ������̖��̓_�����ꗗ
//		try {		
//			misaitenTeikiMap = 
//				ve.mapMiSaitenKyoin(getUPDataAccessObject().find(listTsuiSai),
//						misaitenTeikiMap);
//		} catch (NoSuchDataException e) {
//			// �Y���f�[�^���Ȃ��ꍇ����B
//		} finally {
//       	//2007-01-17 �N���[�Y�R��Ή�
//        	dao.clearSQL();
//      }
//UPEX-410 ��������ƒǍĎ����̌������W�v���� 2008.1.11 K.Tamotsu End
		if (misaitenTeikiMap.isEmpty()) {
    		throw new 
			NoSuchDataException("�w�肵���f�[�^��������܂���ł����B");
		}

		List list = ve.listMiSaitenKyoin(
				getDbSession(), misaitenTeikiMap, kyoinSu, jugyoSu);
		
    	return list;
    }
    
    /**
     *  ��������`�F�b�N
     * 
     * @param list �擾���X�g
     * 
     */
    private void selfTestListMiSaitenKyoinPost(List list) 
    	throws NoSuchDataException {
    	
    	if (list == null) {
    		throw new 
				NoSuchDataException("�w�肵���f�[�^��������܂���ł����B");
    	}
    	
    	for (int i = 0;i < list.size(); i++) {
    	    MisaitenJugyoDTO dto = (MisaitenJugyoDTO)list.get(i);
    		
    		if (dto == null) {
    			throw new BusinessRuleException("���̓_���Ƃ�null �G���[");
    		}
    		if (dto.getKyoinMei() == null) {
    			throw new BusinessRuleException("��������null �G���[");
    		}
    		
    	}
    }
    
    /**
     *  ��������`�F�b�N
     * 
     * @param list �擾���X�g
     * 
     */
    private void selfTestListSaitenUnyouPost(List list) 
    	throws NoSuchDataException {
    	
    	if (list == null) {
    		throw new 
				NoSuchDataException("�w�肵���f�[�^��������܂���ł����B");
    	}
    	
    	for (int i = 0;i < list.size(); i++) {
    		SaitenUnyoListDTO dto = (SaitenUnyoListDTO)list.get(i);
    		
    		if (dto.getKanriBsyoCd() == null) {
    			throw new BusinessRuleException("�Ǘ������R�[�h��null �G���[");
    		}
    		
    		if (dto.getShikenKbn() == null) {
    			throw new BusinessRuleException("�����敪��null �G���[");
    		}

//    		 V1.2�Ή� 2009/10/14 k.higashida Start
    		if (dto.getShikenKaisu() == null) {
    			throw new BusinessRuleException("�����񐔂�null �G���[");
    		}
//    		 V1.2�Ή� 2009/10/14 k.higashida End
    		
    		if (dto.getSaitenKbn() == null) {
    			throw new BusinessRuleException("�̓_���@�敪��null �G���[");
    		}
    		
    		if (dto.getSaitenKaishibi() == null) {
    			throw new BusinessRuleException("�̓_�J�n����null �G���[");
    		}

    		if (dto.getSaitenShuryobi() == null) {
    			throw new BusinessRuleException("�̓_�I������null �G���[");
    		}   		
    	}
    }
    
    /**
     * 
     * �w�肵���̓_�^�p���@���擾����
     * 
     * @param condition ��������
     * @return rgstStnUyhh �̓_�^�p���@DTO
     * @throws NoSuchDataException
     */
    public SaitenUnyoHohoDTO acquireSaitenUnyoHoho(SaitenUnyoCondition condition)
    	throws NoSuchDataException {

        // ���O�����`�F�b�N
        selfTestAcquireSaitenUnyoHohoPre(condition);
        
        // ��񐶐�
        SaitenUnyoHohoDTO rgstStnUyhh = acquireSaitenUnyoHohoMaker(condition);
		
		// ��������`�F�b�N
        selfTestAcquireSaitenUnyoHohoPost(rgstStnUyhh);
        
        return rgstStnUyhh;
    }

    /**
     * ��������`�F�b�N
     * 
	 * @param rgstStnUyhh �̓_�^�p���@
	 */
	private void selfTestAcquireSaitenUnyoHohoPost(
	        SaitenUnyoHohoDTO idoSaitenUnyoDTO) {

    	if (idoSaitenUnyoDTO == null) {
    		throw new BusinessRuleException("�w�肵���̓_�^�p���@�o�^������ null");
    	}		
    	
		if (idoSaitenUnyoDTO.getKanriBusyoCd() == null) {
			throw new BusinessRuleException("�Ǘ������R�[�h�� null");
		}
	
		if (idoSaitenUnyoDTO.getShikenKbn() == null) {
			throw new BusinessRuleException("�����敪�� null");
		}
	
		if (idoSaitenUnyoDTO.getSaitenHohoKbn() == null) {
			throw new BusinessRuleException("�̓_���@�敪�� null");
		}
		
		if (idoSaitenUnyoDTO.getTorokuKaishibi() == null) {
			throw new BusinessRuleException("�̓_�o�^�J�n������ null");
		}
	
		if (idoSaitenUnyoDTO.getTorokuShuryobi() == null) {
			throw new BusinessRuleException("�̓_�o�^�I�������� null");
		}  

		if (idoSaitenUnyoDTO.getIdoKbn() == null) {
			throw new BusinessRuleException("�ٓ��敪���X�g�� null");
		}
	}

	/**
	 * ��񐶐�
	 * 
	 * @param condition
	 * @return SaitenUnyoHohoDTO
	 * @throws DbException
	 * @throws NoSuchDataException
	 */
	private SaitenUnyoHohoDTO acquireSaitenUnyoHohoMaker(
			SaitenUnyoCondition condition) throws NoSuchDataException {

		// SQL��ݒ� �̓_�^�p���@�ꗗ���(�ٓ��敪�ȊO)�擾
		final SQLHelper sqlHelper =
							new SQLHelper(ISQLContents.ID_KM + "00110");
		// SQL��ݒ� �̓_�^�p���@�ꗗ���擾
		getUPDataAccessObject().setSQL(sqlHelper);

		// �f�[�^�ϊ��I�u�W�F�N�g�𐶐�
		// �̓_�^�p���@�ꗗ���擾
		final SaitenUnyoExchanger ve = new SaitenUnyoExchanger();
		SaitenUnyoHohoDTO idoSaitenUnyoDTO = null;
		try {
			idoSaitenUnyoDTO = 
	        	ve.acquireSaitenUnyoHoho(getUPDataAccessObject().find(
	            ve.preparedStatementBindingValues(condition)));
		} finally {
        	//2007-01-17 �N���[�Y�R��Ή�
			getUPDataAccessObject().clearSQL();
        }
		
		// SQL��ݒ� �̓_�^�p���@�ꗗ���(�ٓ��敪)�擾
		final SQLHelper sqlHelperIdo =
							new SQLHelper(ISQLContents.ID_KM + "00111");
		// SQL��ݒ� �̓_�^�p���@�ꗗ���擾
		getUPDataAccessObject().setSQL(sqlHelperIdo);
		// �f�[�^�ϊ��I�u�W�F�N�g�𐶐�
		// �̓_�^�p���@�ꗗ���擾
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
        	//2007-01-17 �N���[�Y�R��Ή�
        	getUPDataAccessObject().clearSQL();
        }
	    return saitenUnyoDTO ;
	}

	/**
	 * ���O�����`�F�b�N
	 * 
	 * @param condition
	 */
	private void selfTestAcquireSaitenUnyoHohoPre(SaitenUnyoCondition condition) {

        if (condition == null) {
            throw new BusinessRuleException("���������� null �͐ݒ�ł��܂���B");
        }
        
        if (condition.getKanriBsyoCd() == null) {
            throw new BusinessRuleException("�Ǘ������R�[�h�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException("�J�u�N�x�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException("�w��NO�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException("�����敪�� null �͐ݒ�ł��܂���B");
        }
	}

    /**
     * 
     * �w�肵���̓_�^�p���@��o�^����
     * 
     * @param condition �X�V����
     * @throws AlreadyUpdatePossibilityException ���ɍX�V����Ă���\��������ꍇ�̗�O�ł��B
     * @throws AlreadyUpdateException ���Ƀf�[�^���X�V����Ă���ꍇ
     */
    public void registSaitenUnyoHoho(SaitenUnyoHohoDTO saitenUnyoHohoDTO)
    	throws 	AlreadyUpdateException, 
    	AlreadyUpdatePossibilityException {
        // ���O�����`�F�b�N
        selfTestRegistSaitenUnyoHohoPre(saitenUnyoHohoDTO);
		// ��񐶐�
		insertRegistSaitenUnyoHoho(saitenUnyoHohoDTO);
    }
   
	/**
	 * ���O�����`�F�b�N
	 * 
	 * @param condition �X�V����
	 */
	private void selfTestRegistSaitenUnyoHohoPre(SaitenUnyoHohoDTO condition) {

        if (condition == null) {
            throw new BusinessRuleException(
                    "�X�V������ null �͐ݒ�ł��܂���B");
        }
        
        if (condition.getKanriBusyoCd() == null) {
            throw new BusinessRuleException(
                    "�Ǘ������R�[�h�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException(
                    "�J�u�N�x�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException(
                    "�w��NO�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException(
                    "�����敪�� null �͐ݒ�ł��܂���B");
        }

//      V1.2�Ή� 2009/10/14 k.higashida Start
        if (condition.getShikenKaisu() == null) {
            throw new BusinessRuleException(
                    "�����񐔂� null �͐ݒ�ł��܂���B");
        }
//      V1.2�Ή� 2009/10/14 k.higashida End
        
        if (condition.getTorokuKaishibi() == null) {
            throw new BusinessRuleException(
                    "�̓_�o�^�J�n���� null �͐ݒ�ł��܂���B");
        }

        if (condition.getTorokuShuryobi() == null) {
            throw new BusinessRuleException(
                    "�̓_�o�^�I������ null �͐ݒ�ł��܂���B");
        }

        if (condition.getSaitenHohoKbn() == null) {
            throw new BusinessRuleException(
                    "�̓_���@�敪�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getIdoKbn() == null) {
            throw new BusinessRuleException(
                    "�ٓ��敪�� null �͐ݒ�ł��܂���B");
        }
	}
	
	/**
	 * DB�o�^����
	 * 
     * @param condition �X�V����
     * @throws AlreadyUpdatePossibilityException ���ɍX�V����Ă���\��������ꍇ�̗�O�ł��B
     * @throws AlreadyUpdateException ���Ƀf�[�^���X�V����Ă���ꍇ
	 */
	private void insertRegistSaitenUnyoHoho(
		SaitenUnyoHohoDTO condition) throws 
		AlreadyUpdateException, 
		AlreadyUpdatePossibilityException {
		try {

			// �̓_�^�pDAO����
			KmcStnUnyoDAO stnUnyoDAO = (KmcStnUnyoDAO)
				getDbSession().getDao(KmcStnUnyoDAO.class);
			KmcStnUnyoAR stnUnyoAR = stnUnyoDAO.findByPrimaryKey(
				condition.getKanriBusyoCd(),
				condition.getKaikoNendo().intValue(),
				condition.getGakkiNo().intValue(),
//			 V1.2�Ή� 2009/10/14 k.higashida Start
//				condition.getShikenKbn().toString());
				condition.getShikenKbn().toString(),
				condition.getShikenKaisu().intValue()
				);
//			 V1.2�Ή� 2009/10/14 k.higashida End
						
			// �V�K
			if (stnUnyoAR == null) {
				// �o�^���ݒ�
				stnUnyoAR = new KmcStnUnyoAR(
						getDbSession(),
						condition.getKanriBusyoCd(),
						condition.getKaikoNendo().intValue(),
						condition.getGakkiNo().intValue(),
//				 V1.2�Ή� 2009/10/14 k.higashida Start
//						condition.getShikenKbn());
						condition.getShikenKbn(),
						condition.getShikenKaisu().intValue());
//				 V1.2�Ή� 2009/10/14 k.higashida End
				
				stnUnyoAR.setTorokuKaishibi(condition.getTorokuKaishibi());
				// Timestamp�̈������ۗ�
				// condition.getSaitenEndTimestamp());
				stnUnyoAR.setTorokuShuryobi(condition.getTorokuShuryobi());
				// Timestamp�̈������ۗ�
				// condition.getSaitenEndTimestamp());
				stnUnyoAR.setSaitenKbn(condition.getSaitenHohoKbn());
				// �̓_�^�pTBL�ɓo�^
				stnUnyoAR.store();
/*
 2006.07.18 �ٓ��ҍ̓_���O�敪�̐ݒ�����{���Ȃ��悤�ɕύX
				// �ٓ��ҍ̓_���O�敪TBL�X�V
				// INSERT
				// �ٓ��ҍ̓_���O�敪DAO����
				KmcStnIdoshaDAO kmcStnIdoshaDAO = (KmcStnIdoshaDAO) 
					getDbSession().getDao(KmcStnIdoshaDAO.class);
				// �ٓ��敪��
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
		            // �o�^�ς݂������ꍇ�A�G���[
					if (kmcStnIdoshaAR != null) {
			            throw new BusinessRuleException(
			                "kmc_Stn_Idosha�e�[�u���ɊY���f�[�^�����݂��܂��B");
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
				
			// �X�V
			} else {

				stnUnyoAR.setTorokuKaishibi(condition.getTorokuKaishibi());
				// Timestamp�̈������ۗ�
				// condition.getSaitenEndTimestamp());
				stnUnyoAR.setTorokuShuryobi(condition.getTorokuShuryobi());
				// Timestamp�̈������ۗ�
				// condition.getSaitenEndTimestamp());
				stnUnyoAR.setSaitenKbn(condition.getSaitenHohoKbn());
				stnUnyoAR.store();
/*	
 2006.07.18 �ٓ��ҍ̓_���O�敪�̐ݒ�����{���Ȃ��悤�ɕύX			
				// �ٓ��ҍ̓_���O�敪TBL�X�V
				// DELETE & INSERT

				// DELETE
   			    // DAO�̎擾
   			    final UPDataAccessObject dao = getUPDataAccessObject();
			    SQLHelper delSqlHelperIdo = 
			        new SQLHelper(ISQLContents.ID_KM + "00112");
			    UtilLog.debug(this.getClass(),
			    			"\n--- sqlHelper = [" + delSqlHelperIdo + "]");
				dao.setSQL(delSqlHelperIdo);

				// �폜�L�[�ݒ�
				List listConditionDel = new ArrayList();
				listConditionDel.add(condition.getKanriBusyoCd());
				listConditionDel.add(condition.getKaikoNendo());
				listConditionDel.add(condition.getGakkiNo());
				listConditionDel.add(condition.getShikenKbn());
								
			    try{
					// �폜���s
					getUPDataAccessObject().remove(listConditionDel);
				} catch (NoSuchDataException e) {
				    // �f�[�^�O�����L�蓾��ׁA�폜�Ώۃ��R�[�h�������ꍇ��
				    // �X���[����
				}

		        // INSERT				
				// �ٓ��ҍ̓_���O�敪DAO����
				KmcStnIdoshaDAO kmcStnIdoshaDAO = (KmcStnIdoshaDAO)
					getDbSession().getDao(KmcStnIdoshaDAO.class);
				KmcStnIdoshaAR kmcStnIdoshaAR = null;
				// �ٓ��敪��
		        List idoDtoList = (List)condition.getIdoKbn();
		        for (int i = 0; i < idoDtoList.size(); i++){
		            String idokbn = (String)idoDtoList.get(i);

					// �o�^���ݒ�
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
     * �w�肵���̓_�^�p���@���폜����
     * 
     * @param condition �̓_�^�p��������
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * @throws AlreadyUpdatePossibilityException ���ɍX�V����Ă���\��������ꍇ�̗�O
     * @throws AlreadyUpdateException ���Ƀf�[�^���X�V����Ă���ꍇ�̗�O
     */
    public void delete(SaitenUnyoCondition condition)
    	throws NoSuchDataException,
    	AlreadyUpdateException, 
    	AlreadyUpdatePossibilityException {

        // ���O�����`�F�b�N
        selfTestDeletePre(condition);
		// �폜�������{
		deleteSaitenUnyoHoho(condition);
    }
    
	/**
	 * ���O�����`�F�b�N
	 * 
	 * @param condition �̓_�^�p��������
	 */
	private void selfTestDeletePre(SaitenUnyoCondition condition) {

        if (condition == null) {
            throw new BusinessRuleException
            	("���������� null �͐ݒ�ł��܂���B");
        }
        
        if (condition.getKanriBsyoCd() == null) {
            throw new BusinessRuleException
            	("�Ǘ������R�[�h�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getKaikoNendo() == null) {
            throw new BusinessRuleException
            	("�J�u�N�x�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getGakkiNo() == null) {
            throw new BusinessRuleException
            	("�w��NO�� null �͐ݒ�ł��܂���B");
        }

        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException
            	("�����敪�� null �͐ݒ�ł��܂���B");
        }
//      V1.2�Ή� 2009/10/14 k.higashida Start
        if (condition.getShikenKaisu() == null) {
            throw new BusinessRuleException
            	("�����񐔂� null �͐ݒ�ł��܂���B");
        }
//      V1.2�Ή� 2009/10/14 k.higashida End
        
	}
	
	/**
	 * DB�폜����
	 * 
     * @param condition �̓_�^�p��������
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * @throws AlreadyUpdatePossibilityException ���ɍX�V����Ă���\��������ꍇ�̗�O
     * @throws AlreadyUpdateException ���Ƀf�[�^���X�V����Ă���ꍇ�̗�O
	 */
	private void deleteSaitenUnyoHoho(SaitenUnyoCondition condition) 
		throws 	NoSuchDataException, 
				AlreadyUpdateException, 
				AlreadyUpdatePossibilityException {
	    try {

	        // �̓_�^�p�e�[�u��
	        // �̓_�^�pDAO����
			KmcStnUnyoDAO stnUnyoDAO = (KmcStnUnyoDAO) 
				getDbSession().getDao(KmcStnUnyoDAO.class);
			KmcStnUnyoAR stnUnyoAR = stnUnyoDAO.findByPrimaryKey(
				condition.getKanriBsyoCd(),
				condition.getKaikoNendo().intValue(),
				condition.getGakkiNo().intValue(),
//				 V1.2�Ή� 2009/10/14 k.higashida Start
//				condition.getShikenKbn());
				condition.getShikenKbn(),
				condition.getShikenKaisu().intValue()
				);
//				 V1.2�Ή� 2009/10/14 k.higashida End

			if (stnUnyoAR == null) {
				throw new NoSuchDataException
					("�w�肵���f�[�^��������܂���B");
			}
			// �̓_�^�pTBL����f�[�^�폜
			stnUnyoAR.remove();

/*
 2006.07.18 �ٓ��ҍ̓_���O�敪�̐ݒ�����{���Ȃ��悤�ɕύX
	        // �ٓ��ҍ̓_���O�敪�e�[�u��
		    // DAO�̎擾
		    final UPDataAccessObject dao = getUPDataAccessObject();
		    SQLHelper delSqlHelperIdo = 
		        new SQLHelper(ISQLContents.ID_KM + "00112");
		    UtilLog.debug(this.getClass(),
		    			"\n--- sqlHelper = [" + delSqlHelperIdo + "]");
			dao.setSQL(delSqlHelperIdo);

			// �폜�L�[�ݒ�
			List listConditionDel = new ArrayList();
			listConditionDel.add(condition.getKanriBsyoCd());
			listConditionDel.add(condition.getKaikoNendo());
			listConditionDel.add(condition.getGakkiNo());
			listConditionDel.add(condition.getShikenKbn());

		    try{
				// �폜���s
				getUPDataAccessObject().remove(listConditionDel);
			} catch (NoSuchDataException e) {
			    // �f�[�^�O�����L�蓾��ׁA�폜�Ώۃ��R�[�h�������ꍇ��
			    // �X���[����
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
     * �]������擾����
     * 
     * @param condition �����ʕ]�����������
     * @return hyokaKijunList<HyokaKijun> �]������X�g
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    public List listShikenBetuHyokaKijun(ShikenBetuHyokaKijunCondition condition)
    	throws NoSuchDataException {
        // ���O�����`�F�b�N
        selfTestListShikenBetuHyokaKijunPre(condition);
        // ��񐶐�
        List hyokaKijunList = listShikenBetuHyokaKijunMaker(condition);
        
        return hyokaKijunList;
    }

	/**
	 * 
	 * ���O�����`�F�b�N
	 * 
	 * @param condition �����ʕ]�����������
	 * 
	 */
	private void selfTestListShikenBetuHyokaKijunPre(
	    ShikenBetuHyokaKijunCondition condition) {

        if (condition == null) {
            throw new BusinessRuleException
            	("�]�������������NULL�łȂ�");
        }
        
        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException
            	("�]������������̊Ǘ��ԍ���NULL�łȂ�");
        }

        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException
            	("�]������������̎����敪��NULL�łȂ�");
        }
        
	}


    /**
     * 
     * ��񐶐�
     * 
	 * @param condition �����ʕ]�����������
     * @return hyokaKijunList<HyokaKijun> �]������X�g
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
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
    		// �P�����݂��Ȃ������ꍇ�B
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
    		// �P�����݂��Ȃ������ꍇ�B
    		if (!sikenKbnFlg) {
    		    list = clList;
    		}
        }    	
    	return list;
    }
    
    /**
     * �w�肳�ꂽ�����ŏ��O�Ώۂ̈ٓ��敪���ꗗ�ɂ��܂��B
     * 
     * @param condition ��������
     * @return �ٓ��敪�̈ꗗ
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
			throw new BusinessRuleException("���������� null �͐ݒ�ł��܂���B");
		}
		if (condition.getJugyoCode() == null) {
			throw new BusinessRuleException("���������̎��ƃR�[�h�� null �͐ݒ�ł��܂���B");
		}
		if (condition.getShikenKubun() == null) {
			throw new BusinessRuleException("���������̎����敪�� null �͐ݒ�ł��܂���B");
		}
	}

	private void testListJogaiTaishoIdoKubunPost(List list) {
		if (list == null) {
			throw new BusinessRuleException("�������ʂ̈ꗗ�� null �͐ݒ�ł��܂���B");
		}
		
		final Iterator ite = list.iterator();
		while (ite.hasNext()) {
			final Object obj = ite.next();
			if (obj == null) {
				throw new BusinessRuleException("�ꗗ�̓��e�� null �͐ݒ�ł��܂���B");
			}
			if (obj instanceof String == false) {
				throw new BusinessRuleException("�ꗗ�̓��e�ɕ�����ȊO�͐ݒ�ł��܂���B");
			}
		}
	}

	
	
	
    /**
     * 
     * �ǍĎ�����MAX�t���O�ɕR�Â��f�_�̍ő�l���擾����
     * 
     * @param condition �����ʕ]�����������
     * @return String �f�_TO
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    public int acquireShikenBetuSotenMax(ShikenBetuHyokaKijunCondition condition)
    	throws NoSuchDataException {
        // ���O�����`�F�b�N
        selfTestAcquireShikenBetuSotenMaxPre(condition);
        // ��񐶐�
        int sotenMax = getAcquireShikenBetuSotenMax(condition);
        
        return sotenMax;
    }
	
	/**
	 * 
	 * ���O�����`�F�b�N
	 * 
	 * @param condition �����ʕ]�����������
	 * 
	 */
	private void selfTestAcquireShikenBetuSotenMaxPre(
	    ShikenBetuHyokaKijunCondition condition) {

        if (condition == null) {
            throw new BusinessRuleException
            	("�]�������������NULL�łȂ�");
        }
        
        if (condition.getKanriNo() == null) {
            throw new BusinessRuleException
            	("�]������������̊Ǘ��ԍ���NULL�łȂ�");
        }

        if (condition.getShikenKbn() == null) {
            throw new BusinessRuleException
            	("�]������������̎����敪��NULL�łȂ�");
        }

        if (!(condition.getShikenKbn().equals(String.valueOf(ShikenUPKbn.TSUISHIKEN.getCode())) || 
                condition.getShikenKbn().equals(String.valueOf(ShikenUPKbn.SAISHIKEN.getCode())))) {
               throw new BusinessRuleException
               	("�]������������̎����敪��1��2�ȊO�����͂���Ă��܂�");
           }


	}

   /**
     * 
     * ��񐶐�
     * 
	 * @param condition �����ʕ]�����������
     * @return String �f�_To
     * @throws NoSuchDataException �w�肵���f�[�^��������Ȃ������ꍇ�̗�O
     * 
     */
    private int getAcquireShikenBetuSotenMax(
            ShikenBetuHyokaKijunCondition condition)
    	throws NoSuchDataException {
        
        int sotenTo = 0;
    	
    	// DAO�̎擾
    	final UPDataAccessObject dao = getUPDataAccessObject();
    	
    	// ���ISQL�̐���
		final AbstractDynamicSQLContentsFactory factory =
			AbstractDynamicSQLContentsFactory.getFactory(
					AbstractDynamicSQLContentsFactory.JG00043);

		final SQLHelper sqlHelper =
			new SQLHelper(factory.create(condition));

    	// �f�[�^�ϊ��I�u�W�F�N�g(VE)�𐶐�
    	final SaitenValueExchanger ve = new SaitenValueExchanger();

    	// SQL��ݒ�
    	dao.setSQL(sqlHelper);

	    try { 
	    	// VE�Ɍ����������Z�b�g
	    	// DB����K�v�ȏ����擾����B
	    	sotenTo = 
	    	    ve.acquireShikenBetuSotenMax(
	    	    		getDbSession() ,getUPDataAccessObject().find(
	    	    				ve.preparedStatementBindingValues(
	    	    				        condition)));
	    } finally {
	    	//2007-01-17 �N���[�Y�R��Ή�
	    	dao.clearSQL();
	    }
	    
        return sotenTo;
    }

    
    /**
     * ���ɓo�^����Ă���̓_�o�^��񂩂�폜�ΏۂƂȂ�f�[�^�𒊏o���܂��B(�������)
     * @param con
     * @return list
     */
    public List listSaitenIkkatsuDeleteTargetTeiki(
    		SaitenIkkatsuDeleteCondition con) throws NoSuchDataException {
    	
    	//���O����
    	selfTestListSaitenIkkatsuDeleteTargetPre(con);
    	
    	//��񐶐�
    	List list = doListSaitenIkkatsuDeleteTargetTeiki(con);
    	
    	//�������
    	selfTestListSaitenIkkatsuDeleteTargetTeikiPost(list);
    	
    	return list;
    }
    
    
    /**
     * ���O�����`�F�b�N
     * @param con
     */
    private void selfTestListSaitenIkkatsuDeleteTargetPre(
    									SaitenIkkatsuDeleteCondition con) {
    	if (con == null) {
    		throw new BusinessRuleException("����������NULL�ł��B");
    	}
    }
	
    /**
     * ��񐶐�
     * @param con
     * @return list
     */
    private List doListSaitenIkkatsuDeleteTargetTeiki(
    			SaitenIkkatsuDeleteCondition con) throws NoSuchDataException {
    
    	// DAO�̎擾
    	final UPDataAccessObject dao = getUPDataAccessObject();
    	
    	
    	List teikiList;
    	List tsuisaiList;
    	
    	// ���ISQL�̐���
		final AbstractDynamicSQLContentsFactory factory =
			AbstractDynamicSQLContentsFactory.getFactory(
					AbstractDynamicSQLContentsFactory.KM00147);
		
		//�������
		final SQLHelper sqlHelper =
			new SQLHelper(factory.create(con));		
		
		SaitenIkkatsuDeleteExchanger ve = 
			new SaitenIkkatsuDeleteExchanger();

    	// SQL��ݒ�
    	dao.setSQL(sqlHelper);
//        UtilLog.debug(this.getClass(),
//  			  "\n--- sqlHelper = [" + sqlHelper + "]");
        try {
			teikiList = 
				ve.listDeleteTargetTeiki(dao.find(
					ve.preparedStatementBindingValuesTeiki(con)));
        } finally {
        	//2007-01-17 �N���[�Y�R��Ή�
        	dao.clearSQL();
        }
			
		return teikiList;
    }
    
    /**
     * ��������`�F�b�N
     * @param list
     */
    private void selfTestListSaitenIkkatsuDeleteTargetTeikiPost(List list) {
    	if (list == null) { //���肦�܂���
    		throw new BusinessRuleException("�擾�������NULL�ł��B");
    	}
    	
    	for (int i = 0; i < list.size(); i++ ) {
    		SaitenIkkatsuDeleteTargetDTO dto =
    			(SaitenIkkatsuDeleteTargetDTO)list.get(i);
    		
    		if (dto.getKaikoNendo() < 1900 || dto.getKaikoNendo() > 9999) {
    			throw new BusinessRuleException("�J�u�N�x���L���͈͊O�ł��B");
    		}
    		
    		if (dto.getGakkiNo() < 1 ) {
    			throw new BusinessRuleException("�w��NO��0�ȉ��ł��B");
    		}
    		
    		if (dto.getJugyoCd() == null) {
    			throw new BusinessRuleException("���ƃR�[�h��NULL�ł��B");
    		}
    		
    	}
    }
    
    /**
     * ���ɓo�^����Ă���̓_�o�^��񂩂�폜�ΏۂƂȂ�f�[�^�𒊏o���܂��B(�ǍĎ���)
     * @param con
     * @return list
     */
    public List listSaitenIkkatsuDeleteTargetTsuisai(
    		SaitenIkkatsuDeleteCondition con) throws NoSuchDataException {
    	
    	//���O����
    	selfTestListSaitenIkkatsuDeleteTargetPre(con);
    	
    	//��񐶐�
    	List list = doListSaitenIkkatsuDeleteTargetTsuisai(con);
    	
    	//�������
    	selfTestListSaitenIkkatsuDeleteTargetTsuisaiPost(list);
    	
    	return list;
    }
    
    	
    /**
     * ��񐶐�
     * @param con
     * @return list
     */
    private List doListSaitenIkkatsuDeleteTargetTsuisai(
    			SaitenIkkatsuDeleteCondition con) throws NoSuchDataException {
    
    	// DAO�̎擾
    	final UPDataAccessObject dao = getUPDataAccessObject();
    	
    	List tsuisaiList;
    	
    	// ���ISQL�̐���
		final AbstractDynamicSQLContentsFactory factory =
			AbstractDynamicSQLContentsFactory.getFactory(
					AbstractDynamicSQLContentsFactory.KM00148);
		
		SaitenIkkatsuDeleteExchanger ve = 
			new SaitenIkkatsuDeleteExchanger();
       
		//�ǍĎ���
		final SQLHelper sqlHelper =
			new SQLHelper(factory.create(con));

    	// SQL��ݒ�
    	dao.setSQL(sqlHelper);
//        UtilLog.debug(this.getClass(),
//  			  "\n--- sqlHelper = [" + sqlHelper + "]");
    	try {
			tsuisaiList =
				ve.listDeleteTargetTsuisai(dao.find(
					ve.preparedStatementBindingValuesTsuisai(con)));
    	} finally {
        	//2007-01-17 �N���[�Y�R��Ή�
        	dao.clearSQL();
        }
			
		return tsuisaiList;
    }
    
    /**
     * ��������`�F�b�N
     * @param list
     */
    private void selfTestListSaitenIkkatsuDeleteTargetTsuisaiPost(List list) {
    	if (list == null) { //���肦�܂���
    		throw new BusinessRuleException("�擾�������NULL�ł��B");
    	}
    	
    	for (int i = 0; i < list.size(); i++ ) {
    		SaitenIkkatsuDeleteTargetDTO dto =
    			(SaitenIkkatsuDeleteTargetDTO)list.get(i);
    		
    		if (dto.getKaikoNendo() < 1900 || dto.getKaikoNendo() > 9999) {
    			throw new BusinessRuleException("�J�u�N�x���L���͈͊O�ł��B");
    		}
    		
    		if (dto.getGakkiNo() < 1 ) {
    			throw new BusinessRuleException("�w��NO��0�ȉ��ł��B");
    		}
    		
    		if (dto.getJugyoCd() == null) {
    			throw new BusinessRuleException("���ƃR�[�h��NULL�ł��B");
    		}
    		
    	}
    }

// 2007/02/27 �s��Ǘ��ꗗ�FNo.3621 Start -->>

	/**
	 * �ǍĎ��]�����~�b�g�`�F�b�N
	 * @param	String		�w�Дԍ�
	 * @param	shikenFlg	�����敪
	 * @param	soten		�f�_
	 * @return	ret			�ǍĎ��]�����~�b�g���ʁF[0]���ʁ^[1]�f�_MAX
	 */
	public int[] checkMaxSoten(	String	gakusekiCd,
									int		shikenFlg,
									int		soten) throws DbException, NoSuchDataException {

        // �w�Џ��擾
		CobGaksekiUPDAO cobGaksekiUPDAO	= (CobGaksekiUPDAO)this.getDbSession().getDao(CobGaksekiUPDAO.class);
        CobGaksekiUPAR	cobGaksekiUPAR	= (CobGaksekiUPAR)cobGaksekiUPDAO.findCurrentByGakusekiCd(	gakusekiCd,
        																							UtilDate.cnvSqlDate(DateFactory.getInstance()));

		// �w�������擾
		GakuseiService		gakuseiSvc = new GakuseiService(this);
		GakuseiHeaderDTO	gakuseiDto = gakuseiSvc.acquireHeaderInfomation(new Long(cobGaksekiUPAR.getKanriNo()));

		// �ǍĎ��]�����~�b�g�`�F�b�N
		return checkMaxSoten(gakuseiDto,shikenFlg,soten);
	}

	/**
	 * �ǍĎ��]�����~�b�g�`�F�b�N
	 * @param	kanriNo		�Ǘ��ԍ�
	 * @param	shikenFlg	�����敪
	 * @param	soten		�f�_
	 * @return	ret			�ǍĎ��]�����~�b�g���ʁF[0]���ʁ^[1]�f�_MAX
	 */
	public int[] checkMaxSoten(	long	kanriNo,
									int		shikenFlg,
									int		soten) throws DbException, NoSuchDataException {

		// �w�������擾
		GakuseiService		gakuseiSvc = new GakuseiService(this);
		GakuseiHeaderDTO	gakuseiDto = gakuseiSvc.acquireHeaderInfomation(new Long(kanriNo));

		// �ǍĎ��]�����~�b�g�`�F�b�N
		return checkMaxSoten(gakuseiDto,shikenFlg,soten);
	}

	/**
	 * �ǍĎ��]�����~�b�g�`�F�b�N
	 * @param	gakuseiDto	�w�����
	 * @param	shikenFlg	�����敪
	 * @param	soten		�f�_
	 * @return	ret			�ǍĎ��]�����~�b�g���ʁF[0]���ʁ^[1]�f�_MAX
	 */
	public int[] checkMaxSoten(	GakuseiHeaderDTO	gakuseiDto,
									int					shikenFlg,
									int					soten) throws DbException {

		int ret[] = {MAX_SOTEN_CHK_NO_ERR,SOTEN_FROM};	// [0]���ʁ^[1]�f�_MAX

		// ��������̏ꍇ�Atrue�ɂă`�F�b�N���s��Ȃ��B
        if(shikenFlg == ShikenUPKbn.TEIKISHIKEN.getCode()) {
			return ret;
		}

        // �]������擾
        List hykList = getHykList(gakuseiDto);

        // �]�����~�b�g�̃����N�ȉ����}�b�v�Ɋi�[
        Map			hykLimitMap			= new LinkedHashMap();	// �]�����~�b�gMap
        boolean	hykLimitOver		= false;				// �]�����~�b�g���߃t���O
        int			maxSotenLimit		= -1;					// �]�����~�b�g�ł̕]��MAX�f�_
        int			maxSotenLimitFromto	= -1;					// �]�����~�b�g��SOTEN_FROM �` SOTEN_TO�Ԃł̕]��MAX�f�_
        Iterator it =hykList.iterator();
		while(it.hasNext()){
			KmzHykUPAR kmzHykUPAR = (KmzHykUPAR)it.next();

			// �]�����~�b�g�𒴉߂��Ă��Ȃ��ꍇ�́A�]�����~�b�gMap�Ɋi�[
			if(!hykLimitOver) {
				hykLimitMap.put(kmzHykUPAR.getHyokaCd(),kmzHykUPAR);
				// �ǎ�
				if(shikenFlg == ShikenUPKbn.TSUISHIKEN.getCode()) {
					if(kmzHykUPAR.isTuisiHyokaMax()) {
						hykLimitOver = true;
					}
				// �Ď�
				} else if(shikenFlg == ShikenUPKbn.SAISHIKEN.getCode()) {
					if(kmzHykUPAR.isSaisiHyokaMax()) {
						hykLimitOver = true;
					}
				}

				// �]�����~�b�g�ł̕]��MAX�f�_
				maxSotenLimit = kmzHykUPAR.getSotenTo().intValue();

				// �]�����~�b�g��SOTEN_FROM �` SOTEN_TO�Ԃł̕]��MAX�f�_���擾
				if(kmzHykUPAR.getSotenTo().intValue() <= SOTEN_TO) {
					maxSotenLimitFromto = kmzHykUPAR.getSotenTo().intValue();
				}

				// �]�����~�b�g����Ȃ烋�[�v�����I��
				if(hykLimitOver) {
					
				}
			}
		}

		// ���̎��_�ŕ]�����~�b�g���߃t���O�yfalse�z�͕]�����~�b�g���ݒ肳��Ă��Ȃ��Ƃ݂Ȃ��B
		// ���̏ꍇ�A�ȍ~�̏����͍s��Ȃ��ׁA�����ŏ����I��
		if(!hykLimitOver) {
			return ret;
		}

		// �ǍĎ��]�����~�b�g�`�F�b�N
		ret = checkMaxSoten(soten,hykList,hykLimitMap,maxSotenLimit,maxSotenLimitFromto);

		return ret;
    }

	/**
	 * �]������i�]�������N�~���j���擾����
	 * @param	gakuseiDto	�w�����
	 * @return	hykList		�]������
	 */
	private List getHykList(GakuseiHeaderDTO gakuseiDto) throws DbException {

        List hykList = null;

        // �]������擾
        KmzHykUPDAO	kmzHykUPDAO	= (KmzHykUPDAO)this.getDbSession().getDao(KmzHykUPDAO.class);
     				hykList		= kmzHykUPDAO.findByHyokaCombo(	gakuseiDto.getMinashiNyugakuNendo().intValue(),
     															gakuseiDto.getMinashiNyugakuGakkiNo().intValue(),
																gakuseiDto.getCurriculumGakkaCode());

		// �]�������N�~���Ƀ\�[�g
		KmzHykARComparator sort = new KmzHykARComparator();
		sort.desc(KmzHykARComparator.HYOKA_RANK);
		Collections.sort(hykList,sort);

		return hykList;
	}

	/**
	 * �ǍĎ��]�����~�b�g�`�F�b�N
	 * @param	soten				�f�_
	 * @param	hykList				�]������
	 * @param	hykLimitMap			�]�����~�b�gMap
	 * @param	maxSotenLimit		�]�����~�b�g�ł̕]��MAX�f�_
	 * @param	maxSotenLimitFromto	�]�����~�b�g��SOTEN_FROM �` SOTEN_TO�Ԃł̕]��MAX�f�_
	 * @return	ret					�ǍĎ��]�����~�b�g���ʁF[0]���ʁ^[1]�f�_MAX
	 */
	private int[] checkMaxSoten(	int		soten,
									List	hykList,
									Map		hykLimitMap,
									int		maxSotenLimit,
									int		maxSotenLimitFromto) {

		int ret[] = {MAX_SOTEN_CHK_NO_ERR,SOTEN_FROM};	// [0]���ʁ^[1]�f�_MAX

		// �f�_�ɊY������]���R�[�h���擾
		String hykCd = getHykCd(soten,hykList);

		// ��L�܂łŕK�v�ȏ����擾�����ׁA���L�ȍ~�̓`�F�b�N�������{
		// �]�����~�b�g���ɑf�_�����݂���ꍇ�̓`�F�b�NOK
		if((KmzHykUPAR)hykLimitMap.get(hykCd) != null) {
			return ret;
		}

		if(soten >= SOTEN_FROM && soten <= SOTEN_TO) {
        	ret[0] = MAX_SOTEN_CHK_ERR_1;
        	ret[1] = maxSotenLimitFromto;
			return ret;
		}

		// �f�_��SOTEN_FROM�`SOTEN_TO�͈̔͊O
        ret[0] = MAX_SOTEN_CHK_ERR_2;
        ret[1] = maxSotenLimit;
		return ret;
    }

	/**
	 * �f�_����]���R�[�h���擾����
	 * @param	soten	�f�_
	 * @param	hykList	�]������
	 * @return	hykCd	�]���R�[�h
	 */
	public String getHykCd(	int		soten,
							List	hykList) {

		String hykCd = null;	// �]���R�[�h

		// �f�_�ɊY������]���R�[�h���擾
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
	 * �ǍĎ��]�����~�b�g�`�F�b�N���̃G���[���b�Z�[�W���擾����
	 * @param	ret	�ǍĎ��]�����~�b�g���ʁF[0]���ʁ^[1]�f�_MAX
	 * @return	msg	���b�Z�[�W
	 */
	public String getCheckMaxSotenErrMsg(int ret[]) {

		String msg = null;

		// �G���[�Ȃ�
		if(ret[0] == MAX_SOTEN_CHK_NO_ERR) {
			return msg;
		// ���~�b�g���߂őf�_���ySOTEN_TO�z�ȉ�
		} else if(ret[0] == MAX_SOTEN_CHK_ERR_1) {
			msg = UtilProperty.getMsgString(KmMsgConst.KMC_MSG_0008E, String.valueOf(ret[1]));
			return msg;
		// ���~�b�g���߂őf�_���ySOTEN_TO�z���傫��
		} else if(ret[0] == MAX_SOTEN_CHK_ERR_2) {
			msg = UtilProperty.getMsgString(KmMsgConst.KMC_MSG_0020E);
			return msg;
		}

		return msg;
	}
// <<-- End   2007/02/27 �s��Ǘ��ꗗ�FNo.3621
}