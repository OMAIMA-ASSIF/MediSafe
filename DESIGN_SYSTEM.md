# 🎨 MediSafe - Design System Quick Reference

## Color Palette

### Primary Colors (Purple-Blue Gradient)
```
Primary 900:  #1A0B3D (Deep Purple - Darkest)
Primary 800:  #2D1B5E
Primary 600:  #5530A2 (Main Brand Color)
Primary 400:  #7B5DB8
Primary 200:  #B9A5D9
Primary 50:   #F0EBFF (Lightest)
```

### Accent Colors (Energy & Vibrancy)
```
Bright Lime:    #D4FF00 ⚡ (FAB Buttons, Progress Bars)
Vibrant Lime:   #A8E63D 💚 (Secondary Accent)
Orange:         #FF6B4A 🔥 (Warning, Alerts)
Pink:           #FF5E78 💗 (Additional Accent)
```

### Secondary Colors (Smooth Transitions)
```
Teal 600:   #00D4D4 (Primary Secondary)
Teal 400:   #2AEAEA
Teal 50:    #E0FCFC
```

### Status Colors
```
Warning:  #FFB800 ⚠️
Danger:   #FF5E5E 🚨
Success:  #2AEAEA ✅
```

### Neutral Colors
```
Gray 100:        #A0A0A0 (Secondary Text)
Gray 50:         #F5F5F5 (Light Backgrounds)
Background:      #FAFBFF (Main Background)
White:           #FFFFFF
Black:           #1A1A1A
```

---

## Typography

### Heading Styles
- **Large Heading**: 28sp, Bold, Primary 900
- **Sub Heading**: 16sp, Bold, Primary 600
- **Card Title**: 18sp, Bold, White (on gradient cards)

### Body Text
- **Primary**: 16sp, Regular, Primary 900
- **Secondary**: 14sp, Regular, Gray 100
- **Small**: 12sp, Regular, Gray 100

---

## Components

### Buttons
- **Primary Button**: 56dp height, Corner radius 14dp, Primary 600 background
- **FAB Button**: 56dp diameter, Lime accent background, 28dp corner radius
- **Outlined Button**: Stroke 1dp, Primary 200 color

### Input Fields
- **Height**: 56dp
- **Corner Radius**: 14dp
- **Stroke Color**: Primary 200
- **Background**: White
- **Hint Text Color**: Gray 100

### Cards
- **Corner Radius**: 16-20dp
- **Elevation**: 0dp (flat design)
- **Padding**: 16-20dp
- **Stroke**: None (0dp)

### Gradients
```
Header Gradient:
- Angle: 45°
- Start: Primary 600
- End: Primary 800

Card Gradient:
- Angle: 135°
- Start: Primary 600
- End: Primary 800

Stats Gradients:
- Teal: Teal 600 → Teal 400
- Compliance: Lime → Orange
- Warning: Warning → Orange
```

---

## Spacing

### Standard Spacing
```
Extra Small: 4dp
Small:       8dp
Regular:     12dp
Medium:      16dp
Large:       20dp
Extra Large: 24dp
Header:      32dp
```

---

## Animation & Effects

### Recommended Animations
- Button Press: 100ms scale animation (0.95 to 1.0)
- Card Transition: 300ms fade-in
- FAB Press: 200ms rotation + scale
- Bottom Nav: 200ms color transition

### Shadows
- Cards: No shadow (0dp elevation)
- Bottom Nav: 8dp elevation
- FAB: Subtle shadow

---

## Screen-Specific Colors

### Home Fragment
- Header: Primary Gradient
- FAB: Lime Accent
- Text: Primary 900
- Empty State: Gray 100

### Medicine Cards
- Background: Primary Gradient
- Text: White
- Progress: Lime
- Button: Lime Accent

### Stats Fragment
- Header: Primary Gradient
- Card 1 (Medicines): Primary Gradient
- Card 2 (Taken): Teal Gradient
- Card 3 (Compliance): Lime→Orange Gradient
- Card 4 (Low Stock): Orange→Warning Gradient

### Auth Screens
- Accent Banner: Lime
- Input Borders: Primary 200
- Buttons: Primary 600
- Links: Primary 600

---

## Material Design 3 Integration

✅ Used Material3 as parent theme  
✅ Custom color tokens for brand alignment  
✅ Rounded shape appearance (12-20dp)  
✅ Modern bottom navigation with active indicator  
✅ Material button styles throughout  
✅ Consistent elevation system  

---

## Implementation Notes

1. **All colors** are defined in `values/colors.xml` - update once, apply everywhere
2. **All styles** are in `values/themes.xml` - maintain consistency
3. **Gradients** are XML drawables - easy to adjust or animate
4. **Border colors** use color selectors for state changes
5. **Text sizes** follow Material3 guidelines

---

## Dark Mode Considerations

Consider creating a dark theme variant in `values-night/colors.xml`:
- Invert backgrounds (light → dark)
- Increase text contrast
- Adjust gradient opacity
- Keep accent colors vibrant

---

**Design System Version: 1.0**  
**Last Updated: 2026-05-07**  
**Status: Ready for Production** ✨
