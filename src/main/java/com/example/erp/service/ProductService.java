package com.example.erp.service;

import com.example.erp.dto.ProductForm;
import com.example.erp.entity.Product;
import com.example.erp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //조회 성능 최적화
public class ProductService {

    private final ProductRepository productRepository;
    private final AiService aiService;

    /**
     * 엑셀 업로드 (SI 핵심 기능)
     * 대량의 데이터를 한 번에 넣을 때 사용
     */
    @Transactional //쓰기 작업이 있으므로 readOnly 해제
    public void uploadExcel(MultipartFile file) throws IOException {

        // 엑셀 파일 읽기(try-with-resources로 메모리 누수 방지)
        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){

            //첫 번째 시트 가져오기
            Sheet sheet = workbook.getSheetAt(0);
            List<Product> productList = new ArrayList<>();

            //행 반복
            for(int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row == null) continue;

                //셀 데이터 읽어서 엔티티로 변환
                String name = getCellValueAsString(row.getCell(0));

                // 2. 숫자는 헬퍼 메서드(getIntValue)를 통해 안전하게 읽음
                int price = getCellValueAsInt(row.getCell(1));
                int stock = getCellValueAsInt(row.getCell(2));
                int safetyStock = getCellValueAsInt(row.getCell(3));
                String description = getCellValueAsString(row.getCell(4));

                //엑셀 데이터 유효성 검사
                if (name.trim().isEmpty()) {
                    throw new IllegalArgumentException((i+1) + "번째 줄 오류: 상품명은 필수입니다.");
                }
                if (price < 100) {
                    throw new IllegalArgumentException((i+1) + "번째 줄 오류: 가격은 100원 이상이어야 합니다. (입력값: " + price + ")");
                }
                if (stock < 0 || safetyStock < 0) {
                    throw new IllegalArgumentException((i+1) + "번째 줄 오류: 재고는 0개 이상이어야 합니다.");
                }
                //빌터 패턴으로 객체 생성
                Product product = Product.builder()
                        .name(name)
                        .price(price)
                        .stockQuantity(stock)
                        .safetyStock(safetyStock)
                        .description(description)
                        .build();
                productList.add(product);
            }
            //DB에 저장
            List<Product> savedList = productRepository.saveAll(productList);

            //AI 기억 저장소에 동기화
            aiService.addProducts(savedList);
        }
    }
    //헬퍼 메서드
    private int getCellValueAsInt(Cell cell){
        if(cell == null) return 0;

        if(cell.getCellType() == CellType.NUMERIC){
            //숫자면 바로 리턴
            return (int) cell.getNumericCellValue();
        }else if(cell.getCellType() == CellType.STRING){
            //"1000"처럼 문자로 된 숫자면 파싱
            try{
                String value = cell.getStringCellValue().replace(",", "").trim();
                return Integer.parseInt(value);
            }catch(NumberFormatException e){
                return 0; //숫자가 아닌 문자면 0으로 처리
            }
        }
        return 0; //빈 칸이면 0
    }
    //셀 데이터를 무조건 문자로
    private String getCellValueAsString(Cell cell){
        if(cell == null) return "";

        if(cell.getCellType() == CellType.STRING){
            return cell.getStringCellValue();
        }else if(cell.getCellType() == CellType.NUMERIC){
            return String.valueOf((int)cell.getNumericCellValue());
        }
        return "";
    }

    //전체 상품 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
    @Transactional
    public void createProduct(Product product) {
        //DB저장
        Product savedProduct = productRepository.save(product);

        //AI 동기화
        aiService.addProduct(savedProduct);
    }
    /**
     * 상품 수정
     * Dirty Checking: 값을 바꾸면 트랜잭션 끝날 때 알아서 UPDATE 쿼리가 나감
     */
    @Transactional
    public void updateProduct(Long id, ProductForm form){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 없습니다."));

        product.setName(form.getName());
        product.setPrice(form.getPrice());
        product.setStockQuantity(form.getStockQuantity());
        product.setSafetyStock(form.getSafetyStock());
        product.setDescription(form.getDescription());

        aiService.addProduct(product);
    }
    //상품 하나만 가져오기
    public Product getProduct(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 없습니다."));
    }
}
