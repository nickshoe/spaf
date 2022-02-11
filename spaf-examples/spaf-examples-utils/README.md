# SPAF Examples Utils

### Kafka Image Receiver

```bash
java -jar kafka-image-receiver.jar <bootstrap server> <images dir> <topic name>
```

### Kafka Image Sender

```bash
java -jar kafka-image-sender.jar <bootstrap server> <images dir> <topic name>
```

## Sender Params

Params values for each app:
- Face Recognition:
  - `<images dir>` = `/home/datalab/local/dataset/Youtube/`
  - `<topic name>` = `FACE`
- OCR:
  - `<images dir>` = `/home/datalab/local/dataset/ocr/`
  - `<topic name>` = `OCRimages`
- Plates:
  - `<images dir>` = `/home/datalab/local/dataset/Plate/`
  - `<topic name>` = `PlateImages2`
