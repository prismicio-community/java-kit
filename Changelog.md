### 1.0-M12 (2014-09-19)

**Breaking change**: The content in StructuredText is now escaped. This was a bug that had to be fixed, but if you relied on it to include custom HTML it will no longer work. You can use a custom html serializer to get the behavior you need.

Bugfixes:

  - \#33 The content in StructuredText is now escaped.

Features:

  - Links in images
  - Timestamp support

### 1.0-M11 (2014-09-19)

Features:

  - Custom HTML serializer for StructuredText.asHtml()
  - Labels for blocks and spans

