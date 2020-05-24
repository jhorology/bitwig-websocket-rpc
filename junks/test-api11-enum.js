const { BitwigClient } = require('..')

const wait = (millis) => new Promise(resolve => setTimeout(resolve, millis))

async function main() {
  const bws = new BitwigClient('ws://localhost:8887')
  await bws.connect()
  await bws.config({
    useTransport: true
  })
  const enumDef = await bws.call('transport.automationWriteMode.enumDefinition')
  console.info('transport.automationWriteMode.enumDefinition():', enumDef)
  for (var valueDef of enumDef) {
    console.info('value definition:', valueDef)
    const valueDef1 = await bws.call('transport.automationWriteMode.enumDefinition.valueDefinitionAt', [valueDef.valueIndex])
    console.info(`valueDefinitionAt(${valueDef.valueIndex}):`, valueDef1)
    const valueDef2 = await bws.call('transport.automationWriteMode.enumDefinition.valueDefinitionFor', [valueDef.id])
    console.info(`valueDefinitionFor('${valueDef.id}'):`, valueDef2)
  }
  bws.close()
}
main()
  .then(() => console.log('all test done!'))
  .catch((err) => console.log('done with error!', err))
