import {
  Box, Button, Calendar, Carousel, Chart, CheckBox, Clock,
  Diagram, Distribution, DropButton, Anchor, FormField,
  Heading, Image, RangeSelector, DataTable, Accordion, AccordionPanel,
  Menu, Meter, Paragraph, RadioButton, RangeInput,
  Select, Stack, Table, TableBody, TableCell, TableHeader, TableRow,
  Text, TextArea, TextInput, Video, WorldMap,
} from 'grommet';
import { Add, LinkNext, Grommet as GrommetIcon, Descend } from 'grommet-icons';
import RoutedButton from '../components/RoutedButton';
import Page from '../components/Page';
import Section from '../components/Section';
import Item from '../components/Item';
import ColorRoll from '../components/ColorRoll';

const CHART_VALUES = [
  { value: [7, 90], label: 'ninety' },
  { value: [6, 80], label: 'eighty' },
  { value: [5, 60], label: 'sixty' },
  { value: [4, 70], label: 'seventy' },
  { value: [3, 60], label: 'sixty' },
  { value: [2, 40], label: 'forty' },
  { value: [1, 30], label: 'thirty' },
  { value: [0, 10], label: 'ten' },
];

const stringOptions = ['small', 'medium', 'large', 'xlarge', 'huge'];

export default class Home extends React.Component {
  state = { values: [3, 7] };
  render() {
    const { values } = this.state;
    return (
      <Page title='Explore'>
        <Box pad='large'>
          <Box direction='row' gap='xlarge' margin={{ bottom: 'large' }}>
            <Box basis='large' overflow='hidden'>
              <Heading level={1}>
                <strong>bitwig-websocket-rpc demo</strong>
              </Heading>
              <Paragraph size='large' margin='none'>
This is an experimental site built with <strong>Grommet 2</strong> and <strong>Next.js</strong>.
Visit the official <Anchor href='https://v2.grommet.io/' target='_blank'>Grommet site</Anchor> for the latest updates.
              </Paragraph>
            </Box>
          </Box>
        </Box>
        <Box pad={{ horizontal: 'large' }}>
          <Section name='Start' index={0}>
            <Item name='Image' path='/image'>
              <Image
                fit='cover'
                src='//v2.grommet.io/assets/Wilderpeople_Ricky.jpg'
              />
            </Item>
            <Item name='Image' path='/image'>
              <Image
                fit='cover'
                src='//v2.grommet.io/assets/Wilderpeople_Ricky.jpg'
              />
            </Item>
            <Item name='Image' path='/image'>
              <Image
                fit='cover'
                src='//v2.grommet.io/assets/Wilderpeople_Ricky.jpg'
              />
            </Item>
            <Item name='Image' path='/image'>
              <Image
                fit='cover'
                src='//v2.grommet.io/assets/Wilderpeople_Ricky.jpg'
              />
            </Item>
            <Item name='Image' path='/image'>
              <Image
                fit='cover'
                src='//v2.grommet.io/assets/Wilderpeople_Ricky.jpg'
              />
            </Item>
            <Item name='Image' path='/image'>
              <Image
                fit='cover'
                src='//v2.grommet.io/assets/Wilderpeople_Ricky.jpg'
              />
            </Item>
            <Item name='Image' path='/image'>
              <Image
                fit='cover'
                src='//v2.grommet.io/assets/Wilderpeople_Ricky.jpg'
              />
            </Item>
          </Section>
        </Box>
      </Page>
    );
  }
}
