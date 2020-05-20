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

| attribute         | required | default value | possible values                         |
|-------------------|----------|---------------|-----------------------------------------|
| name              | yes      | none          | any string                              |
| type              | yes      | none          | PAIR, UNPAIRED, ATTR_ONLY or AUTO_EMPTY |
| hasParameters     | no       | false         | true, false                             |
| allowedModifiers  | no       | false         | true, false                             |
| multiLine         | no       | false         | true, false                             |
| deprecated        | no       | false         | true, false                             |
| deprecatedMessage | no       | ""            | any string                              |

### &lt;filter&gt; in &lt;filters&gt;

| attribute    | required | default value | possible values                               |
|--------------|----------|---------------|-----------------------------------------------|
| name         | yes      | none          | any string                                    |
| arguments    | no       | ""            | any string used as filter parameters help     |
| description  | no       | ""            | any string                                    |
| insertColons | no       | ""            | contains double colon per required argument   |

### &lt;variable&gt; in &lt;variables&gt;

| attribute | required | default value | possible values     |
|-----------|----------|---------------|---------------------|
| name      | yes      | none          | any string          |
| type      | yes      | mixed         | PHP type definition |

### &lt;function&gt; in &lt;functions&gt;

| attribute   | required | default value | possible values                   |
|-------------|----------|---------------|-----------------------------------|
| name        | yes      | none          | any string                        |
| returnType  | yes      | mixed         | PHP type definition               |
| arguments   | no       | ""            | Used as method parameters         |
| description | no       | ""            | any string                        |
