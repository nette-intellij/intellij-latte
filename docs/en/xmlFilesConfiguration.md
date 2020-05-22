# XML configuration files

XML configuration files for Latte plugin are files contains information about tags, filters, variables etc.

- Plugin load all files named `latte-intellij.xml` in your project including `vendor` folder or something other folders.
- You can use more `latte-intellij.xml` files. All will be loaded.
- If you are using `Nette < 3` you will probably need: [Default latte-intellij.xml for Nette < 3](https://github.com/nette-intellij/intellij-latte/blob/master/docs/bellowNette3.xml)
  - Copy it to `latte-intellij.xml` located somewhere in your Nette project
- Tip: distribute latte-intellij.xml with your Composer package

This is example file content with sample values:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<latte version="1">
    <tags>
        <tag name="myIf" type="PAIR" hasParameters="true" allowedModifiers="false" deprecated="false" deprecatedMessage="MyIf is deprecated, use xxx instead." />
        <tag name="myFor" type="PAIR" hasParameters="true" allowedModifiers="false" multiLine="true" />
    </tags>
    <variables>
        <variable name="testVar" type="\Student" />
    </variables>
    <functions>
        <function name="testFunction" returnType="bool" arguments="(string $moduleName)" description="Test description" />
    </functions>
    <filters>
        <filter name="myFilter" arguments=":(?limit = 0)" description="Test filter description" insertColons=":" />
    </filters>
</latte>
```

## Allowed XML tags and attributes

**If a mandatory parameter is missing or invalid item will be skipped.** 

### &lt;tag&gt; in &lt;tags&gt;

| attribute         | required | default value | possible values                         | Description                          |
|-------------------|----------|---------------|-----------------------------------------|--------------------------------------|
| name              | yes      | none          | any string                              | Name for tag                         |
| type              | yes      | none          | PAIR, UNPAIRED, ATTR_ONLY or AUTO_EMPTY | Type for tag                         |
| hasParameters     | no       | false         | true, false                             | Used after code completion           |
| allowedFilters    | no       | false         | true, false                             | Used for inspections and completions |
| multiLine         | no       | false         | true, false                             | Used after code completion           |
| deprecated        | no       | false         | true, false                             | Used for inspection                  |
| deprecatedMessage | no       | ""            | any string                              | Message for deprecated tag           |

### &lt;filter&gt; in &lt;filters&gt;

| attribute    | required | default value | possible values                               | Description                                   |
|--------------|----------|---------------|-----------------------------------------------|-----------------------------------------------|
| name         | yes      | none          | any string                                    | Name for filter                               |
| arguments    | no       | ""            | any string used as filter parameters help     | Used for code completion (eg. `:(limit = 1)`) |
| description  | no       | ""            | any string                                    | Used as description for code completion       |
| insertColons | no       | ""            | contains double colon per required argument   | Used after code completion (eg. `batch::`)    |

### &lt;variable&gt; in &lt;variables&gt;

| attribute | required | default value | possible values     | Description                         |
|-----------|----------|---------------|---------------------|-------------------------------------|
| name      | yes      | none          | any string          | Name for PHP variable (without `$`) |
| type      | yes      | mixed         | PHP type definition | PHP type definition (eg. `string`)  |

### &lt;function&gt; in &lt;functions&gt;

| attribute   | required | default value | possible values                   | Description                                 |
|-------------|----------|---------------|-----------------------------------|---------------------------------------------|
| name        | yes      | none          | any string                        | Name for custom function                    |
| returnType  | yes      | mixed         | PHP type definition               | Return type (eg. `string`)                  |
| arguments   | no       | ""            | Used as method parameters         | Used for code completion (eg. `(string $a)` |
| description | no       | ""            | any string                        | Used as description for code completion     |