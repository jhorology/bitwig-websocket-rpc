import React, {forwardRef} from 'react'
import { withStyles } from '@material-ui/core/styles'
import { capitalize } from './utils'

export const styles = (theme) => ({
  /* Styles applied to the root element. */
  root: {
    width: 2,
    height: '100%',
    boxSizing: 'content-box',
    padding: '0, 7px',
    display: 'inline-block',
    position: 'relative',
    color: theme.palette.primary.main,
    WebkitTapHighlightColor: 'transparent',
    '&$disabled': {
      pointerEvents: 'none',
      cursor: 'default',
      color: theme.palette.grey[400]
    },
    '&$horiontal': {
      height: 2,
      width: '100%',
      padding: '7px 0'
    },
    '@media (pointer: coarse)': {
      // Reach 42px touch target, about ~8mm on screen.
      padding: '20px 0',
      '&$vertical': {
        padding: '0 20px'
      }
    },
    '@media print': {
      colorAdjust: 'exact'
    }
  },
  /* Styles applied to the root element if `size="small"`. */
  sizeSmall: {
    width: 40,
    height: 24,
    padding: 7,
    '& $thumb': {
      width: 16,
      height: 16
    },
    '& $switchBase': {
      padding: 4,
      '&$checked': {
        transform: 'translateX(16px)'
      }
    }
  },
  /* Pseudo-class applied to the internal `SwitchBase` component's `checked` class. */
  checked: {},
  /* Pseudo-class applied to the internal SwitchBase component's disabled class. */
  disabled: {},
  /* Styles applied to the internal SwitchBase component's input element. */
  input: {
    left: '-100%',
    width: '300%'
  },
  /* Styles applied to the track element. */
  track: {
    height: '100%',
    width: '100%',
    borderRadius: 14 / 2,
    zIndex: -1,
    transition: theme.transitions.create(['opacity', 'background-color'], {
      duration: theme.transitions.duration.shortest
    }),
    backgroundColor:
      theme.palette.type === 'light' ? theme.palette.common.black : theme.palette.common.white,
    opacity: theme.palette.type === 'light' ? 0.38 : 0.3
  }
});

const VuMeter = forwardRef(function VuMeter(props, ref) {
  const {
    classes,
    className,
    size = 'medium',
    orientation = 'vertical',
    ...other
  } = props;
  return (
    <span
      className={clsx(
        classes.root,
        {
          [classes[`size${capitalize(size)}`]]: size !== 'medium'
        },
        className
      )}
    >
      <span className={classes.track} />
    </span>
  );
})

export default withStyles(styles, { name: 'VuMeter' })(VuMeter)


