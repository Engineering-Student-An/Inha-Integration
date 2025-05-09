package an.inhaintegration.rentalee.service;

import an.inhaintegration.rentalee.domain.FeeStudent;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    public List<FeeStudent> uploadExcel(MultipartFile file) throws IOException {
        List<FeeStudent> result = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = sheet.getRow(i);
            DataFormatter formatter = new DataFormatter();

            String stuId = formatter.formatCellValue(row.getCell(0));
            String name = formatter.formatCellValue(row.getCell(1));

            if(!stuId.isEmpty() && !name.isEmpty()){
                FeeStudent feeStudent = new FeeStudent(stuId, name);
                result.add(feeStudent);
            }

        }
        return result;
    }

    public String validateExcelFile(MultipartFile file) {

        // 업로드 파일이 존재하는지 검증
        if(file == null || file.isEmpty()) return "선택된 파일이 없습니다!";

        // 업로드 된 파일 명
        String fileName = file.getOriginalFilename();

        // 파일 명이 .xlsx , .xls 로 끝나는지 (=엑셀파일인지) 검증
        if (fileName != null && !fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")) return "엑셀 파일을 업로드해주세요!";

        return null;
    }
}
