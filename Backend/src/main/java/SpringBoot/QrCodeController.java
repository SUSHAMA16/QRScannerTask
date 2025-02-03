package SpringBoot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

@RestController
@RequestMapping("/api")
@CrossOrigin  
public class QrCodeController {

    @PostMapping(value = "/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> scanQrCode(@RequestParam("file") MultipartFile file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid image file.");
            }

            
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            
            Result result = new MultiFormatReader().decode(bitmap);
            String qrText = result.getText();
            
            
            Map<String, String> details = parseUpiDetails(qrText);
            if (details.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("QR code does not contain valid UPI details.");
            }
            return ResponseEntity.ok(details);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("QR code not found in the image.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading the image.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing the QR code.");
        }
    }

   
    private Map<String, String> parseUpiDetails(String qrText) {
        Map<String, String> details = new HashMap<>();
        if (qrText != null && qrText.startsWith("upi://pay")) {
            int idx = qrText.indexOf('?');
            if (idx > 0 && idx < qrText.length() - 1) {
                String query = qrText.substring(idx + 1);
               
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        if ("pa".equalsIgnoreCase(keyValue[0])) {
                            details.put("upiId", keyValue[1]);
                        } else if ("pn".equalsIgnoreCase(keyValue[0])) {
                            details.put("merchantName", keyValue[1]);
                        }
                    }
                }
            }
        }
        return details;
    }
}

