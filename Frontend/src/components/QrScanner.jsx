import React, { useEffect, useState } from 'react';
import { Html5QrcodeScanner } from 'html5-qrcode';

const QrScanner = () => {
  const [decodedText, setDecodedText] = useState('');

  useEffect(() => {
    const config = { fps: 10, qrbox: 250 };

    
    const onScanSuccess = (decodedText, decodedResult) => {
      setDecodedText(decodedText);
      
    };

    const onScanFailure = (error) => {
      
    };

    const html5QrcodeScanner = new Html5QrcodeScanner(
      'qr-reader',
      config,
      false
    );
    html5QrcodeScanner.render(onScanSuccess, onScanFailure);

    
    return () => {
      html5QrcodeScanner.clear().catch((err) => {
        console.error('Error:', err);
      });
    };
  }, []);

  return (
    
    <div style={{ padding: '20px', textAlign: 'center' }}>
      <h2>Scan or Upload QR Code for Details</h2>
      <div id="qr-reader" style={{ width: '300px', margin: '0 auto' }}></div>
      {decodedText && (
        <div style={{ marginTop: '20px' }}>
          <h3>Scanned QR Code:</h3>
          <p>{decodedText}</p>
        </div>
      )}
    </div>
   
  );
};

export default QrScanner;
