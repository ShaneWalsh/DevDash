# Dev Dash
## _Build quickly and dashboards easily for multiple environments_

TODO Dev Dash description here, you can build dashbaoard in minutes with auditing/security and can connect to multiple different environments at once.

Completely free and open source.

## Features
- Create a dashboard in minutes.
- Support for multiple different connections for one schema. QA1/2/3 etc
- Join data from different env's/dbs onto one screen.
- Auditing of all user actions. Fish taging of all sessions.
- Security users, roles.
- Supports MySql, SQLserver currently. Rest are WIP :) .

You can use mininal security and audting if you like, create one role and one user and link it to everything. Or you can leverage fine grained user and roles and control exactly what each user can do, and see an audit log for all their actions.
> Dev Dash is amazing!

## Installation
todo details on pulling the docker images and setting up a local deployment.
admin and DB passwords via docker secrets

Create a docker compose file, that should allow you to spin up devdash on your local machine.
```sh
version: '3'

services:

  devdash-ui:
    image: shaneneilwalsh/dev_dash_ui
    networks: 
      - devdash_net
    ports:
      - 8682:80

  devdash-mysql:
    image: mysql:8.0
    networks: 
      - devdash_net
    environment:
      - MYSQL_ROOT_PASSWORD=Example2021
      - MYSQL_DATABASE=dddatabase
      - MYSQL_USER=dduser
      - MYSQL_PASSWORD=Example2021
    ports:
      - 3306:3306
    volumes:
      - myappdd:/var/lib/mysql

  devdash-be:
    image: shaneneilwalsh/dev_dash_be
    networks: 
      - devdash_net
    depends_on:
      - devdash-mysql
      - devdash-ui
    ports:
      - 8683:8683
    environment:
      - db.driver=com.mysql.jdbc.Driver
      - db.url=jdbc:mysql://devdash-mysql:3306/dddatabase?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
      - db.username=dduser
      - db.password=Example2021
      - SPRING_JPA_HIBERNATE_DDL_AUTO=create

networks: 
  devdash_net:

volumes:
  myappdd:
```

```sh
docker-compose up
```

For production environments...
It's recomended you dont connect to a production environment, this is a development tool.

## Configuration of Users and Roles
Can create users, and define roles. The other configs will then link to these roles. Roles are a tree structure, with dev dash admin by default the top most parent role. Each Role has one parent role. When permission checking is done on every operation, the tree will be searched to see if you have the specified role or any parent role of it.

## Configuration of Schema's, Connections and Queries
Each Schema can have multiple connections. Because in dev environment there can be many db's with the same schema. E.g qa1/2/3/preprod etc.
Each Schema can have multiple queries. When a query is executed, the schema and connection will be detected from the UI.

## Configuration of Dashboards, Tabs, Panels and Elements.

WIP table of elements attributes and the different values.

| Attribute | type | Description |
| ------ | ------ | ------ |
| code | string |Every element must have a unique code. I recommend you open maintain a consistent prefix pattern for your entire dashboard. |
| label | string | Label to display on the UI for element |
| replacementCode | string | The value this will replace in queries. Will default to the element code if not specified. Also known as RC. |
| type | string | BUTTON,TEXT,DATE,SELECT,TABLE,PAGINATOR |
| order | number | not implemented yet |
| readOnly | boolean | whether its editable or not. Defaults to false. |
| hidden | boolean | Prevent cluttering the screen with readonlys. Defaults to false. |
| exeQuery | Array[string] | List of queries to execute in order when element is activated. e.g Button click. |
| triggerOnLoad | boolean | false by default. When set to true, the element will trigger when its initialised. e.g good for table loads. |
| triggerOnEmit | string | Trigger will activate when the specified element code emits any updates. e.g this could be a search button listening to a search text box |
| dataOn | string | take the data from this emit and populate data with it |
| dataOnParser | DataParser | the method of data extraction, might be value from a table row, or date etc |
| dataOnParserConfig | string | the method of data extraction, might be value from a table row, or date etc |
| overrideDataOn | boolean | means will replace data when dataOn triggers everytime, even if user has altered it. on by default |
| initialData | any | the elements initial data, can be string/number/array |
| initialDataParser | DataParser | the method of data extraction, might be value from a table row, or date etc |
| initialDataParserConfig | string | the method of data extraction, might be value from a table row, or date etc |
| truncateDataLength | number | truncate the data in display fields. Editable cells still contain full data |

## Table Element
The table element will activate on clicking on a table row. The row data is then exposed in the activation. 
Before activation, query submitted data is null, after activation the row codes are appended to the panel code and exposed for replacement in queries.

> Activation: row values (e.g id) would be exposed via data parsing.
>
> Submission: row values (e.g id) on panel1 would be panel1_id replacementcode for BE to 
use.

| Attribute | type | Description |
| ------ | ------ | ------ |
| columns | string | todo but can be used to add nice column names to tables rather than inferred values.|

## Development

Want to contribute? Great!

Java, Angular.
TODO

## Docker

todo 


## License

MIT

**Free Software, Hell Yeah!**
