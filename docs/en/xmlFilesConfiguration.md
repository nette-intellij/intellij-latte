# XML configuration files

XML configuration files for Latte plugin are files contains information about tags, filters, variables etc.

- Plugin load all files named `latte-intellij.xml` in your project including `vendor` folder or something other folders except ignored folders.
- You can use more `latte-intellij.xml` files. All will be loaded, but must have another value in vendor attribute.
- If you are using `Nette < 3` you will probably need: [Default latte-intellij.xml for Nette < 3](https://github.com/nette-intellij/intellij-latte/blob/master/docs/bellowNette3.xml) or [current plugin XML configuration](https://github.com/nette-intellij/intellij-latte/blob/master/src/main/resources/xmlSources)
  - Copy it to `latte-intellij.xml` located somewhere in your Nette project
  - **But it is not necessary.** Plugin contains this basic tags, filters etc. But if you need rewrite it, you can use above file as template
  - In newer version of Latte/Nette is `latte-intellij.xml` part of appropriate packages in vendor
- **Tip:** distribute `latte-intellij.xml` with your Composer package

This is example file content with sample values:

- Replace `myVendor/myPackage` to your unique vendor name

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE latte PUBLIC "-//LATTE//Latte plugin XML V0.0.1//EN" ".idea/intellij-latte/xmlSources/Latte.dtd">
<latte version="1" vendor="myVendor/myPackage">
    <tags>
        <tag name="myIf" type="PAIR" arguments="condition" deprecatedMessage="MyIf is deprecated, use {if ...} instead." />
        <tag name="myFor" type="PAIR" arguments="initialization; condition; afterthought" allowedFilters="true" multiLine="true" />
        <tag name="block" type="PAIR" allowedFilters="true" multiLine="true">
            <arguments>
                <argument name="name" types="PHP_IDENTIFIER,VARIABLE,PHP_EXPRESSION" validType="string" required="true" />
            </arguments>
        </tag>
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

**If a required parameter is missing or invalid item will be skipped.** 

### &lt;tag&gt; in &lt;tags&gt;

| attribute         | required | default value | possible values                         | Description                          |
|-------------------|----------|---------------|-----------------------------------------|--------------------------------------|
| name              | yes      | none          | any string                              | Name for tag                         |
| type              | yes      | none          | PAIR, UNPAIRED, ATTR_ONLY or AUTO_EMPTY | Type for tag                         |
| arguments         | no       | ""            | any string                              | Used after code completion as help   |
| allowedFilters    | no       | false         | true, false                             | Used for inspections and completions |
| multiLine         | no       | false         | true, false                             | Used after code completion           |
| deprecatedMessage | no       | ""            | any string                              | Message for deprecated tag           |

#### Attribute `type` detail:

- `PAIR` - it means pair tag like `{form}{/form}`
- `UNPAIRED` - it means unpaired tag like `{varType}`
- `ATTR_ONLY` - it means tag used only as n:attribute, like `n:class`
- `AUTO_EMPTY` - it means pair tag which can be used as unpaired like `{label} | {label}{/label}`

#### Attribute `arguments` detail:

- Attribute `arguments` can contains string which will be shown as help after code completion
- If attribute `arguments` is not provided, it means "tag without arguments"
- Instead of attribute `argumnets` you can define arguments as tag which is child of `<arguments>` in `<tag>` 
- If you use argument settings via: `<argument>` tags, help for code completion will be generated automatically
- But you can still use argument `arguments` if using `<argument>` tags, then this value will rewrite automatically generated help after code completion

### &lt;argument&gt; in &lt;arguments&gt; in &lt;tag&gt;

| attribute         | required | default value | possible values                         | Description                          |
|-------------------|----------|---------------|-----------------------------------------|--------------------------------------|
| name              | yes      | none          | any string                              | Name for tag argument                |
| types             | yes      | none          | _Types are listed under this table_     | Argument  types separated by comma   |
| validType         | no       | mixed         | PHP type definition                     | Expected return type for argument    |
| required          | no       | false         | true, false                             | Used for inspections and completions |
| repeatable        | no       | false         | true, false                             | Used for inspections and completions |

#### Attribute `types` detail:

- `PHP_IDENTIFIER` - match with `foo`, `bar`, `foo_123`, ...
- `PHP_EXPRESSION` - match with `$var`, `foo()`, `\Bar::`, `... (-> | :: property|method|constant)`
- `PHP_CONDITION` - match with `PHP_EXPRESSION` (only use `condition` as name for completion)
- `PHP_CLASS_NAME` - match with class names `\Foo`, `\Foo\Bar`, ...
- `VARIABLE` - match with `$var`
- `VARIABLE_DEFINITION` - match with `$var` and mark it as definition
- `VARIABLE_DEFINITION_EXPRESSION` - match with `[type] $variable = expr`, and mark variable as definition
- `VARIABLE_DEFINITION_ITEM` - match with `[type] $var` and mark variable as definition
- `BLOCK` - match with `#block`
- `BLOCK_USAGE` - match with `PHP_IDENTIFIER` (only use `block` as name for completion)
- `NONE` - match with string `none`
- `PHP_TYPE` - match with php type like `string`, `string|null`, `\Foo\Bar`, ...
- `CONTENT_TYPE` - match with content type like `application/json`, ...
- `LINK_DESTINATION` - match with `default`, `Foo:detail`, `handleFoo!`, ...
- `LINK_PARAMETERS` - match with `PHP_EXPRESSION` or `KEY_VALUE`
- `KEY_VALUE` - match with `, [var =>] value`
- `CONTROL` - match with `VARIABLE` (only use `$control` as name for completion)


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