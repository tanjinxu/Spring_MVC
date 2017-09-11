package com.oforsky.mat.util.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.oforsky.mat.data.PlanRoundQueryBean;
import com.oforsky.mat.dlo.PlanRoundDlo;
import com.oforsky.mat.dlo.RoundComparisionDlo;
import com.oforsky.mat.dlo.TestCaseDlo;
import com.oforsky.mat.ebo.CaseResultEbo;
import com.oforsky.mat.ebo.MatConstant;
import com.oforsky.mat.ebo.PlanRoundEbo;
import com.oforsky.mat.ebo.RoundComparisionEbo;
import com.oforsky.mat.ebo.TaskEbo;
import com.oforsky.mat.ebo.TestCaseEbo;
import com.oforsky.mat.ebo.TestResultEnum;
import com.oforsky.mat.ebo.TestRoundComparisionResultTypeEnum;
import com.oforsky.mat.util.CommonUtil;
import com.truetel.jcore.data.QueryOperEnum;
import com.truetel.jcore.data.T3File;
import com.truetel.jcore.util.DirUtil;
import com.truetel.jcore.util.FileUtil;

public class BuildRoundComparisionResultUtil {
    private static final Logger log = Logger.getLogger(BuildRoundComparisionResultUtil.class);

    private static final String ROUND_COMPARISION_PLANROUND_TEMPLATE_PATH = DirUtil.getAppDataHome(
            new TaskEbo().getAppName()) + File.separator + MatConstant.ROUND_COMPARISION_PLANROUND_TEMPLATE_NAME;
    private static final String ROUND_COMPARISION_PLANROUND_RESULT_PATH = DirUtil
            .getT3TmpHome(new RoundComparisionEbo().getAppName()) + File.separator;
    private static final int ROUND_COMPARISION_PLANROUND_SHEET_INDEX = 0;
    private static final int ROUND_COMPARISION_PLANROUND_PLAN_ROW = 2;
    private static final int ROUND_COMPARISION_PLANROUND_CYCLE_ROW = 3;
    private static final int ROUND_COMPARISION_PLANROUND_RESULT_ROW = 10;
    private static final int ROUND_COMPARISION_PLANROUND_ROUND_CELL_NUMBER = 6;
    private static final String ROUND_COMPARISION_NONE_LABEL = "NA";
    private static final String ROUND_COMPARISION_PASSED_LABEL = "Passed";
    private static final String ROUND_COMPARISION_FAIL_SAME_REASON_LABEL = "Fail By Same Reason";
    private static final String ROUND_COMPARISION_FAIL_DIFFERENT_REASON_LABEL = "Fail By Different Reason";
    private static final String ROUND_COMPARISION_EXECUTION_TYPE = "Automated";

    public static void createComparitionResultForPlanRounds(RoundComparisionEbo roundComparision) throws Exception {
        log.debug("Try to createComparitionResultForPlanRounds...");
        List<Integer> planRoundOidList = roundComparision.getCompRoundOidList();
        Map<Integer, List<CaseResultEbo>> planRoundCaseResultMap = PlanReportUtil
                .getUniqueResultCaseResultMapWithPlanRoundOidList(planRoundOidList);
        if (!planRoundCaseResultMap.keySet().containsAll(planRoundOidList)) {
            log.error("createComparitionResultForPlanRounds failed");
            // FIXME with handling
        }

        Map<Integer, ComparisionResult> caseRoundResultMap = new HashMap<Integer, ComparisionResult>();
        for (Integer planRoundOid : planRoundCaseResultMap.keySet()) {
            List<CaseResultEbo> resultList = planRoundCaseResultMap.get(planRoundOid);
            resultList.forEach(caseResult -> {
                try {
                    SingleRoundResult roundResult = new SingleRoundResult(planRoundOid, caseResult.getResult(),
                            caseResult.getErrorTrace(), caseResult.getErrorTraceSignature());
                    ComparisionResult result = caseRoundResultMap.get(caseResult.getCaseOid());
                    if (result == null) {
                        result = new ComparisionResult();
                    }
                    result.addToRoundResultMap(roundResult);
                    caseRoundResultMap.put(caseResult.getCaseOid(), result);
                } catch (Exception e) {
                    log.error("createComparitionResultForPlanRounds failed");
                    // FIXME with handling
                }

            });
        }
        caseRoundResultMap.values().forEach(result -> {
            result.restTotalResult();
        });

        caseRoundResultMap.keySet().forEach(caseOid -> {
            log.debug("Camparision Result:" + caseOid + "," + caseRoundResultMap.get(caseOid).toString());
        });
        BuildRoundComparisionResultUtil.exportPlanRoundComparisionResult(roundComparision, caseRoundResultMap);
    }

    public static void exportPlanRoundComparisionResult(RoundComparisionEbo roundComparision,
            Map<Integer, ComparisionResult> caseRoundResultMap) throws Exception {
        String resultFilePath = ROUND_COMPARISION_PLANROUND_RESULT_PATH + roundComparision.getCompName() + ".xlsx";
        FileUtil.copyFile(new File(ROUND_COMPARISION_PLANROUND_TEMPLATE_PATH), new File(resultFilePath), true);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(new File(resultFilePath));
            Workbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = (XSSFSheet) wb.getSheetAt(ROUND_COMPARISION_PLANROUND_SHEET_INDEX);
            List<Integer> planRoundOidList = roundComparision.getCompRoundOidList();
            appendPlanInfoForPlanRoundComarision(planRoundOidList, sheet);
            appendCaseResultForPlanRoundComarision(planRoundOidList.size(), caseRoundResultMap, sheet);
            fos = new FileOutputStream(new File(resultFilePath));
            wb.write(fos);
            fos.flush();

            T3File t3 = new T3File(FileUtil.readFile(resultFilePath), roundComparision.getCompName() + ".xlsx");
            roundComparision.setCompResultFile(t3);
            new RoundComparisionDlo().update(roundComparision);
        } finally {
            CommonUtil.closeResources(fis, fos);
        }

    }

    private static void appendPlanInfoForPlanRoundComarision(List<Integer> planRoundOidList, XSSFSheet sheet)
            throws Exception {
        XSSFRow planRow = sheet.createRow(ROUND_COMPARISION_PLANROUND_PLAN_ROW);
        XSSFRow cycleRow = sheet.createRow(ROUND_COMPARISION_PLANROUND_CYCLE_ROW);

        PlanRoundQueryBean qb = new PlanRoundQueryBean();
        qb.setPlanRoundOidArr(planRoundOidList.toArray(new Integer[planRoundOidList.size()]));
        qb.setPlanRoundOidOper(QueryOperEnum.In);
        qb.setOrderBy(PlanRoundEbo.ATTR_PlanRoundOid);
        qb.setAscendant(true);

        List<PlanRoundEbo> planRoundList = new PlanRoundDlo().list(qb);
        for (int index = 0; index < planRoundList.size(); index++) {
            PlanRoundEbo planRound = planRoundList.get(index);
            planRow.createCell(index).setCellValue(planRound.getTestPlanName());
            cycleRow.createCell(index).setCellValue(planRound.getTestCycleId());
        }
    }

    private static void appendCaseResultForPlanRoundComarision(int roundListSize,
            Map<Integer, ComparisionResult> caseRoundResultMap, XSSFSheet sheet) throws Exception {

        int startRow = ROUND_COMPARISION_PLANROUND_RESULT_ROW;
        int resultIndex = roundListSize * ROUND_COMPARISION_PLANROUND_ROUND_CELL_NUMBER + 1;
        Set<Integer> keySet = caseRoundResultMap.keySet();
        Iterator iterator = keySet.iterator();
        TestCaseDlo testCaseDlo = new TestCaseDlo();
        while (iterator.hasNext()) {
            Integer caseOid = (Integer) iterator.next();
            TestCaseEbo testCaseEbo = testCaseDlo.get(caseOid);
            if (testCaseEbo == null) {
                continue;
            }
            ComparisionResult result = caseRoundResultMap.get(caseOid);
            XSSFRow row = sheet.createRow(startRow++);
            int startCell = 1;

            row.createCell(0).setCellValue(testCaseEbo.getCaseId());
            List<SingleRoundResult> singleResultList = result.getRoundResultList();
            for (int index = 0; index < singleResultList.size(); index++) {
                SingleRoundResult singleResult = singleResultList.get(index);
                row.createCell(startCell++).setCellValue(singleResult.getCaseResult().name());
                row.createCell(startCell++).setCellValue(singleResult.errorTrace);
                row.createCell(startCell++).setCellValue("");
                row.createCell(startCell++).setCellValue("");
                row.createCell(startCell++).setCellValue("");
                row.createCell(startCell++).setCellValue(ROUND_COMPARISION_EXECUTION_TYPE);
            }
            row.createCell(resultIndex).setCellValue(convertTotalResultLabel(result.getTotalResult()));
        }

    }

    private static String convertTotalResultLabel(TestRoundComparisionResultTypeEnum result) {
        switch (result) {
        case Passed:
            return ROUND_COMPARISION_PASSED_LABEL;
        case FailBySameReason:
            return ROUND_COMPARISION_FAIL_SAME_REASON_LABEL;
        case FailByDifferentReason:
            return ROUND_COMPARISION_FAIL_DIFFERENT_REASON_LABEL;
        case None:
        default:
            return ROUND_COMPARISION_NONE_LABEL;
        }
    }

    static class ComparisionResult {
        List<SingleRoundResult> roundResultList;
        TestRoundComparisionResultTypeEnum totalResult;
        boolean containsFailure;

        public ComparisionResult() {
            this.roundResultList = new ArrayList<SingleRoundResult>();
            this.totalResult = TestRoundComparisionResultTypeEnum.None;
            this.containsFailure = false;
        }

        public void addToRoundResultMap(SingleRoundResult roundResult) {
            this.roundResultList.add(roundResult);
            if (!isContainsFailure() && roundResult.getCaseResult().equals(TestResultEnum.Failed)) {
                setContainsFailure(true);
            }
        }

        public void restTotalResult() {
            if (roundResultList.size() == 1) {
                return;
            }
            Set<SingleRoundResult> resultSet = new HashSet<SingleRoundResult>();
            resultSet.addAll(roundResultList);
            if (resultSet.size() == 1) {
                if (isContainsFailure()) {
                    setTotalResult(TestRoundComparisionResultTypeEnum.FailBySameReason);
                } else {
                    setTotalResult(TestRoundComparisionResultTypeEnum.Passed);
                }
            } else {
                setTotalResult(TestRoundComparisionResultTypeEnum.FailByDifferentReason);
            }
        }

        public List<SingleRoundResult> getRoundResultList() {
            return roundResultList;
        }

        public void setRoundResultList(List<SingleRoundResult> roundResultList) {
            this.roundResultList = roundResultList;
        }

        public TestRoundComparisionResultTypeEnum getTotalResult() {
            return totalResult;
        }

        public void setTotalResult(TestRoundComparisionResultTypeEnum totalResult) {
            this.totalResult = totalResult;
        }

        public boolean isContainsFailure() {
            return containsFailure;
        }

        public void setContainsFailure(boolean containsFailure) {
            this.containsFailure = containsFailure;
        }

        @Override
        public String toString() {
            return "ComparisionResult [roundResultList=" + roundResultList + ", totalResult=" + totalResult
                    + ", containsFailure=" + containsFailure + "]";
        }

    }

    static class SingleRoundResult {
        Integer roundOid;
        TestResultEnum caseResult;
        String errorTrace;
        String errorSha1;

        public SingleRoundResult(Integer roundOid, TestResultEnum caseResult, String errorTrace, String errorSha1) {
            this.roundOid = roundOid;
            this.caseResult = caseResult;
            this.errorTrace = errorTrace;
            this.errorSha1 = errorSha1;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SingleRoundResult)) {
                return false;
            }
            SingleRoundResult newResult = (SingleRoundResult) obj;
            if (this.getErrorSha1() == null && newResult.getErrorSha1() == null) {
                return true;
            }
            if (this.getErrorSha1().equals(newResult.getErrorSha1())) {
                return true;
            }
            return false;

        }

        public Integer getRoundOid() {
            return roundOid;
        }

        public void setRoundOid(Integer roundOid) {
            this.roundOid = roundOid;
        }

        public TestResultEnum getCaseResult() {
            return caseResult;
        }

        public void setCaseResult(TestResultEnum caseResult) {
            this.caseResult = caseResult;
        }

        public String getErrorTrace() {
            return errorTrace;
        }

        public void setErrorTrace(String errorTrace) {
            this.errorTrace = errorTrace;
        }

        public void setErrorSha1(String errorSha1) {
            this.errorSha1 = errorSha1;
        }

        public String getErrorSha1() {
            return this.errorSha1;
        }

        @Override
        public String toString() {
            return "SingleRoundResult [roundOid=" + roundOid + ", caseResult=" + caseResult + ", errorTrace="
                    + errorTrace + ", errorSha1=" + errorSha1 + "]";
        }

    }
}
